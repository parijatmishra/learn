use std::fmt;

use futures::join;
use log::{info,error,warn};
use tokio::time::delay_for;
use tokio::time::Duration;
use std::fmt::Formatter;
use crate::combineresults::OurError::{LearnSongError, LearnDanceError, SingingError, DancingError};

pub async fn orchestrate() -> String {
    // let (learn_sing, learn_dance, try_sing, try_dance) = (false, true, true, true);
    // let (learn_sing, learn_dance, try_sing, try_dance) = (true, false, true, true);
    // let (learn_sing, learn_dance, try_sing, try_dance) = (true, true, false, false);
    // let (learn_sing, learn_dance, try_sing, try_dance) = (true, true, false, true);
    // let (learn_sing, learn_dance, try_sing, try_dance) = (true, true, true, false);
    let (learn_sing, learn_dance, try_sing, try_dance) = (true, true, true, true);

    let fut1 = learn_to_sing(learn_sing);
    let fut2 = learn_to_dance(learn_dance);

    let (song, dance) = join!(fut1, fut2);

    return match (song, dance) {
        (Ok(song), Ok(dance)) => {
            match sing_and_dance(SongAndDance { song, dance }, try_sing, try_dance).await {
                Ok(s) => format!("CONGRATULATIONS: {}", s),
                Err(e) => format!("ERROR: {}", e)
            }
        }
        _ => "We cannot proceed".to_owned()
    }
}

struct Song {
    name: String,
}

struct Dance {
    name: String,
}

struct SongAndDance {
    song: Song,
    dance: Dance,
}

#[derive(Debug, Clone)]
enum OurError {
    LearnSongError,
    LearnDanceError,
    SingingError,
    DancingError,
    SongAndDanceError,
}

impl fmt::Display for OurError {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "{:?}", self)
    }
}

type OurResult<T> = std::result::Result<T, OurError>;

async fn learn_to_sing(learn_sing: bool) -> OurResult<Song> {
    info!("Learning to sing...");
    delay_for(Duration::new(1, 0)).await;
    let song = Song {
        name: String::from("Thunder Rising"),
    };

    if learn_sing {
        info!("Learnt how to sing {}!", &song.name);
        return Ok(song);
    }
    error!("I have a cold today!");
    return Err(LearnSongError);
}

async fn learn_to_dance(learn_dance: bool) -> OurResult<Dance> {
    info!("Learning to dance...");
    delay_for(Duration::new(1, 0)).await;
    let dance = Dance {
        name: String::from("Flamenco"),
    };

    if learn_dance {
        info!("Learnt to dance {}!", &dance.name);
        return Ok(dance);
    }
    error!("I broke my leg!");
    return Err(LearnDanceError);
}

async fn sing(song: &Song, try_sing: bool) -> OurResult<()> {
    info!("Singing {}...", song.name);
    delay_for(Duration::new(1, 0)).await;

    if try_sing {
        info!("Done singing!");
        return Ok(());
    }
    warn!("I have a cold today!");
    return Err(SingingError);
}

async fn dance(dance: &Dance, try_dance: bool) -> OurResult<()> {
    info!("Dancing {}...", dance.name);
    delay_for(Duration::new(1, 0)).await;

    if try_dance {
        info!("Done dancing!");
        return Ok(());
    }
    warn!("I broke my leg!");
    return Err(DancingError);
}

async fn sing_and_dance(what: SongAndDance, try_sing: bool, try_dance: bool) -> OurResult<String> {
    // It's ok if either singing or dancing has an error
    // but not both
    let fut1 = sing(&what.song, try_sing);
    let fut2 = dance(&what.dance, try_dance);
    let results = join!(fut1, fut2);
    return match results {
        (Ok(_), Ok(_)) => Ok("We sang and danced!".to_owned()),
        (Err(e), Ok(_)) => Ok(format!("We danced, but when singing, had a {}", e)),
        (Ok(_), Err(e)) => Ok(format!("We sang, but when dancing, had a {}", e)),
        _ => {
            error!("Both singing and dancing failed!");
            Err(OurError::SongAndDanceError)
        }
    }
}
