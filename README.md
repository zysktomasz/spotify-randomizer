### Google Cloud Runtime Config Environments Configuration

Google SDK Cloud Runtime documentation: https://cloud.google.com/sdk/gcloud/reference/beta/runtime-config

A number of application properties set in `.yml` files are bootstrapped from Google Cloud Runtime Config environments.
To access them one has to have `Cloud SDK` installed with `beta runtime-config` added.

#### To list configs active for current project

`gcloud beta runtime-config configs list`

#### To list variables set for specific config (prod in this case)

```gcloud beta runtime-config configs variables list --config-name=spotifyrandomizer_prod --values```

#### To create/update variables

`gcloud beta runtime-config configs variables set --config-name=spotifyrandomizer_prod my-var "my value"`

### Manually trigger GCP Cloud Build & Artifact Registry deploy

You can manually trigger Docker image build and deployment of that image to GCP Artifact Registry by using `gcloud sdk`
tools.

In root directory of this project is `cloudbuild.yaml` file that defines configuration for this process.

#### To invoke build and deploy run:

`gcloud builds submit`