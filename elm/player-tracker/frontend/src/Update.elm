module Update exposing (..)

import RemoteData

import Models exposing (Model, Player)
import Msgs exposing (Msg)
import Routing exposing (parseLocation)
import Commands

-- UPDATE
update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Msgs.OnFetchPlayers response ->
            ( { model | players = response }, Cmd.none )
        Msgs.OnLocationChange location ->
            let newRoute = parseLocation location
            in  ( { model | route = newRoute }, Cmd.none )
        Msgs.ChangeLevel player howMuch ->
            let updatePlayer = { player | level = player.level + howMuch }
            in  ( model, Commands.savePlayerCmd updatePlayer )
        Msgs.OnPlayerSave (Ok player) ->
            ( updatePlayer model player, Cmd.none )
        Msgs.OnPlayerSave (Err err) ->
            ( model, Cmd.none )

updatePlayer : Model -> Player -> Model
updatePlayer model updatedPlayer =
    let
        keepOrReplace currentPlayer =
            if updatedPlayer.id == currentPlayer.id then
                updatedPlayer
            else
                currentPlayer

        updatePlayerList players =
            List.map keepOrReplace players

        updatedPlayers = RemoteData.map updatePlayerList model.players
    in
        { model | players = updatedPlayers }
