use futures::join;
use log::info;
use tokio::time::delay_for;
use tokio::time::Duration;

pub async fn orchestrate() -> String {
    let fut1 = learn_to_sing();
    let fut2 = learn_to_dance();
    let (song, dance) = join!(fut1, fut2);
    let song_and_dance = SongAndDance { song, dance };
    let conclusion = sing_and_dance(song_and_dance).await;
    return conclusion;
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

async fn learn_to_sing() -> Song {
    info!("Learning to sing...");
    delay_for(Duration::new(1, 0)).await;
    let song = Song {
        name: String::from("Thunder Rising"),
    };
    info!("Learnt how to sing {}!", &song.name);
    return song;
}

async fn learn_to_dance() -> Dance {
    info!("Learning to dance...");
    delay_for(Duration::new(1, 0)).await;
    let dance = Dance {
        name: String::from("Flamenco"),
    };
    info!("Learnt to dance {}!", &dance.name);
    return dance;
}

async fn sing(song: &Song) {
    info!("Singing {}...", song.name);
    delay_for(Duration::new(1, 0)).await;
    info!("Done singing!");
}

async fn dance(dance: &Dance) {
    info!("Dancing {}...", dance.name);
    delay_for(Duration::new(1, 0)).await;
    info!("Done dancing!");
}

async fn sing_and_dance(what: SongAndDance) -> String {
    let fut1 = sing(&what.song);
    let fut2 = dance(&what.dance);
    join!(fut1, fut2);
    return format!("We sang {} and danced {}", what.song.name, what.dance.name);
}
