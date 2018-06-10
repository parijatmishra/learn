module Msgs exposing (..)

import Models exposing (Player)
import RemoteData exposing (WebData)

-- MESSAGES
type Msg
    = OnFetchPlayers (WebData (List Player))