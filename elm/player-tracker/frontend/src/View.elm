module View exposing (..)

import Html exposing (Html, div, text)
import Models exposing (Model, PlayerId)
import Msgs exposing (Msg)
import Players.List
import Players.Edit
import RemoteData

-- VIEW
view : Model -> Html Msg
view model =
    div []
        [ page model ]

page : Model -> Html Msg
page model =
    case model.route of
        Models.PlayersRoute ->
            Players.List.view model.players
        Models.PlayerRoute id ->
            playerEditPage model id
        Models.NotFoundRoute ->
            notFoundView

playerEditPage : Model -> PlayerId -> Html Msg
playerEditPage model playerId =
    case model.players of
        RemoteData.NotAsked ->
            text ""
        RemoteData.Loading ->
            text "Loading..."
        RemoteData.Success players ->
            let maybePlayer = players
                                |> List.filter (\p -> p.id == playerId)
                                |> List.head
            in 
                case maybePlayer of
                    Just player ->
                        Players.Edit.view player
                    Nothing ->
                        notFoundView
        RemoteData.Failure error ->
            text (toString error)

notFoundView : Html a
notFoundView =
    div []
        [ text "Not Found" ]
