#!/bin/bash

DROPWIZARD_ASSETS_DIR="../src/main/resources/assets"

echo "Removing all old assets except ''static'' ..."
find $DROPWIZARD_ASSETS_DIR -maxdepth 1 ! -name 'static' ! -name 'assets' | xargs rm -Rf 
echo "Copying web application ..."
cp -R dist/* $DROPWIZARD_ASSETS_DIR
echo "Done!"


