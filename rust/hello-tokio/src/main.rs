mod combineresults;
mod simple;

use env_logger;
use log::info;

fn main() {
    env_logger::init();

    let mut rt = tokio::runtime::Runtime::new().unwrap();
    println!("Simple...");
    let conclusion = rt.block_on(simple::orchestrate());
    info!("{}", conclusion);

    println!("Combine Result's...");
    let conclusion = rt.block_on(combineresults::orchestrate());
    info!("{}", conclusion);
}
