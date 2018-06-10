module Update exposing (..)

import Models exposing (Model)
import Msgs exposing (Msg (OnFetchPlayers))

-- UPDATE
update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        OnFetchPlayers response ->
            ( { model | players = response }, Cmd.none )


