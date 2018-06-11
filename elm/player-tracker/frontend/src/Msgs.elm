module Msgs exposing (..)

import Http
import Models exposing (Player)
import RemoteData exposing (WebData)
import Navigation exposing (Location)

-- MESSAGES
type Msg
    = OnFetchPlayers (WebData (List Player))
    | OnLocationChange Location
    | ChangeLevel Player Int
    | OnPlayerSave (Result Http.Error Player)
