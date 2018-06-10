module Main exposing (..)

import Html exposing (program)

import Models exposing (Model, initialModel)
import Msgs exposing (Msg)
import View exposing (view)
import Update exposing (update)


init : ( Model, Cmd Msg )
init =
    ( initialModel, Cmd.none )

-- SUBSCRIPTIONS
subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none

-- MAIN
main : Program Never Model Msg
main =
    program
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }
