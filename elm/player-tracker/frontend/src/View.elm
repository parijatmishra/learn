module View exposing (..)

import Html exposing (Html, div)
import Models exposing (Model)
import Msgs exposing (Msg)
import Players.List

-- VIEW
page : Model -> Html Msg
page model =
    Players.List.view model.players

view : Model -> Html Msg
view model =
    div []
        [ page model ]
