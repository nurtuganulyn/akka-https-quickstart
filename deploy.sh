#!/bin/bash

# Exit the script early if a command returns a non-zero exit code
set -e

# For more information: https://devcenter.heroku.com/articles/container-registry-and-runtime

# Grab the params
readonly APP_NAME= "sis8akka"
readonly IMAGE="e00f9caf5fdd"
readonly HEROKU_API_KEY="8f03e8aa-e9d7-4204-b379-2d86165d604b"

echo "Deploying $IMAGE to $APP_NAME"

# Login to Docker registry
docker login --username=_ --password=$HEROKU_API_KEY registry.heroku.com

# Create Heroku tag
docker tag $IMAGE registry.heroku.com/$APP_NAME/web

# Push Heroku tag
docker push registry.heroku.com/$APP_NAME/web

# Release
curl -f -n -X PATCH -H "Authorization: Bearer $HEROKU_API_KEY" https://api.heroku.com/apps/$APP_NAME/formation \
  -d '{
  "updates": [
    {
      "type": "web",
      "docker_image": "'"$(docker inspect registry.heroku.com/$APP_NAME/web --format={{.Id}})"'"
    }
  ]
}' \
  -H "Content-Type: application/json" \
-H "Accept: application/vnd.heroku+json; version=3.docker-releases"