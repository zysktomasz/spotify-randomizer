Link to React SPA that consumes this
API: [github.com/zysktomasz/spotify-randomizer-web](https://github.com/zysktomasz/spotify-randomizer-web)

### Overview

**SpotifyRandomizer Backend** - handles authenticated communication with Spotify API.

Exposes a few REST endpoints that can be used by clients (such
as [SpotifyRandomizer React Client](http://zysk.it/projects/spotifyrandomizer-web)) to invoke such actions as:
retrieving authenticated user details, their playlists and songs, as well as a way to automatically reorganize order of
songs in selected playlists.

----------

### Technologies used

- Java 16
- Spring Boot 2.4.x
- Docker
- Spotify
  Client ([github.com/thelinmichael/spotify-web-api-java](https://github.com/thelinmichael/spotify-web-api-java))
- Jjwt ([github.com/jwtk/jjwt](https://github.com/jwtk/jjwt))
- MapStruct, Lombok
- Google Cloud Config (Runtime
  environments) ([github.com/GoogleCloudPlatform/spring-cloud-gcp](https://github.com/GoogleCloudPlatform/spring-cloud-gcp))
- Github Actions
- GCP Cloud Build, Cloud Run, Artifact Registry

----------

### Architecture

![spotifyrandomizer backend architecture diagram](https://i.imgur.com/CJqdQuQ.png)

#### Rest API

Application exposes a few API endpoints. It allows to:

- `GET /api/spotify/playlist` - get user playlists
- `GET /api/spotify/playlist/{playlistId}/tracks` - get playlist tracks
- `PUT /api/spotify/playlist/{playlistId}` - reorder songs in playlists

#### Authentication

Application follows **Authorization Code Flow** described
in [Spotify Authorization Guide](https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow)
to authenticate user.

Authentication is done by communication between backend (this project) and Spotify API through usage
of [thelinmichael/spotify-web-api-java](https://github.com/thelinmichael/spotify-web-api-java).

This application secures its endpoints by JWT.

#### Docker

Application is dockerized.

#### Deployment

Docker image is built and deployed to [Google Cloud Run](https://cloud.google.com/run).

Cloud Run is scaled to 0 - meaning that it is turned off when not in use and requires little time to spin up when
invoked.

Entire process of image building and deployment is handled by [Google Cloud Build](https://cloud.google.com/build) and
described later in this document.

----------

### Google Cloud Runtime Config Environments Configuration

To avoid storing sensitive information, like API secrets a number of application properties set in `application.yml`
files are bootstrapped from _Google Cloud Runtime Config_
environments. To access and manage them one has to have `Cloud SDK` installed with `beta runtime-config` added.

[Spring Cloud Google Cloud Config](https://github.com/GoogleCloudPlatform/spring-cloud-gcp) project is used to achieve
this functionality. It allows for application to load these properties from GCP environment during startup.

#### To list configs active for current project

`gcloud beta runtime-config configs list`

#### To list variables set for specific config (prod in this case)

```gcloud beta runtime-config configs variables list --config-name=spotifyrandomizer_prod --values```

#### To create/update variables

`gcloud beta runtime-config configs variables set --config-name=spotifyrandomizer_prod my-var "my value"`

----------

### Continuous Integration & Continuous Deployment

[Github Actions](https://github.com/features/actions) are used to achieve CI/CD. Actions are configured
in [`github-ci.yml`](https://github.com/zysktomasz/spotify-randomizer/blob/master/.github/workflows/github-ci.yml)

Steps done during CI:

1. Install Java SDK on Ubuntu 20.04
2. Load cached maven packages (if available) from previous builds, to speed up process
3. Run tests and build reports (by running `mvn site`)
4. Verify output of previous step (`mvn verify`)
5. Upload reports as Github artifacts - makes it possible to download and analyze

Steps done during CD:

1. Check condition to confirm that _master_ branch can run deployment job.
2. Configure Cloud SDK with credentials stored in _Github Secrets_
3. Build and push Docker image to Google Artifact Registry
4. Deploy image to Cloud Run

```
name: Java Maven CI

on: [ push ]

env:
  PROJECT_ID: ${{ secrets.GCP_PROJECT_ID }}
  SERVICE_ACCOUNT_KEY: ${{ secrets.GCP_SA_KEY }}
  ARTIFACT_REPOSITORY_NAME: spotifyrandomizer-repo
  SERVICE: spotifyrandomizer-backend
  REGION: europe-central2

jobs:
  test_and_build_reports:
    runs-on: ubuntu-20.04
    steps:
      - uses: actions/checkout@v2
      - run: |
          download_url="https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk-16.0.1%2B9/OpenJDK16U-jdk_x64_linux_hotspot_16.0.1_9.tar.gz"
          wget -O $RUNNER_TEMP/java_package.tar.gz $download_url
      - name: Set up JDK 16
        uses: actions/setup-java@v2
        with:
          distribution: 'jdkfile'
          jdkFile: ${{ runner.temp }}/java_package.tar.gz
          java-version: '16.0.1'
          architecture: x64
      - name: Cache Maven packages
        uses: actions/cache@v2
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2
      - name: mvn site - build reports
        run: mvn site
      - name: mvn verify
        run: mvn verify
      - name: Archive project reports (tests and coverage)
        uses: actions/upload-artifact@v2
        with:
          name: reports
          path: target/site
  build_and_release:
    if: ${{ github.ref == 'refs/heads/master' }}
    needs: test_and_build_reports
    runs-on: ubuntu-20.04
    steps:
      - uses: actions/checkout@v2
      - name: Set up Cloud SDK
        uses: google-github-actions/setup-gcloud@master
        with:
          project_id: ${{ env.PROJECT_ID }}
          service_account_key: ${{ env.SERVICE_ACCOUNT_KEY }}
          export_default_credentials: true
      - name: Authorize Docker push
        run: gcloud auth configure-docker europe-central2-docker.pkg.dev
      - name: Build and push to Artifact Registry
        run: |-
          docker build -t europe-central2-docker.pkg.dev/${{ env.PROJECT_ID }}/${{ env.ARTIFACT_REPOSITORY_NAME }}/${{ env.SERVICE }}:${{ github.sha }} .
          docker push europe-central2-docker.pkg.dev/${{ env.PROJECT_ID }}/${{ env.ARTIFACT_REPOSITORY_NAME }}/${{ env.SERVICE }}:${{ github.sha }}
      - name: Deploy to Cloud Run
        run: |-
          gcloud run deploy ${{ env.SERVICE }} \
            --region ${{ env.REGION }} \
            --image europe-central2-docker.pkg.dev/${{ env.PROJECT_ID }}/${{ env.ARTIFACT_REPOSITORY_NAME }}/${{ env.SERVICE }}:${{ github.sha }} \
            --platform "managed" \
```

----------