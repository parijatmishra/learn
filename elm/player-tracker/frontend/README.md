# player-tracker / frontend

## Functionality

This app has two screens:

## Screen 1: Player List (default)

Will show a list of players and their levels.  From here you can:
- Navigate to edit a player.

## Screen 2: Edit Player

Shows the edit view of a player.  In this screen you can:
- Change the level.

## Build 

Building and running requires `yarn`.  If you don't have `yarn`, intall it like this:

```
npm install -g yarn
```

Then, install required packages:

```
yarn install
elm-package install
```

## Run locally

`yarn client` will run the webpack dev server and serve our HTML and JS.

## Create distribution

`yarn build` will create the webpack build and put the bundles in `dist`.
