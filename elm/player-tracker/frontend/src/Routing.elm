module Routing exposing (parseLocation)

import Navigation   exposing (Location)
import UrlParser    exposing ((</>), s, string, top, map, oneOf, Parser, parseHash)
import Models       exposing (PlayerId, Route(..))

matchers : Parser (Route -> a) a
matchers =
    oneOf
        [ map PlayersRoute top
        , map PlayerRoute (s "players" </> string)
        , map PlayersRoute (s "players")
        ]

parseLocation : Location -> Route
parseLocation location =
    case (parseHash matchers location) of
        Just route -> route
        Nothing -> NotFoundRoute