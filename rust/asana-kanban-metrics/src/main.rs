mod asana;
mod config;
mod project;

use clap::{App, Arg};
use env_logger;
use std::fs;
use std::path::{Path, PathBuf};
use tokio;

use config::{parse_config, MyConfig};

fn main() {
    /* Logging */
    env_logger::init();

    /* Command Line */
    let (config_file_str, token_file_str) = process_command_line();

    /*
     * Config data
     */
    let config_file_path = Path::new(&config_file_str)
        .canonicalize()
        .expect(&format!("Bad config file path: {}", &config_file_str));
    let config_str = fs::read_to_string(config_file_path)
        .expect(&format!("Bad config file: {}", &config_file_str));
    let config: MyConfig = parse_config(&config_str);

    /*
     * Asana Personal Access Token -- credentials
     */
    let token_file_path: PathBuf = Path::new(&token_file_str)
        .canonicalize()
        .expect(&format!("Bad token file path: {}", &token_file_str));
    let token_str =
        fs::read_to_string(token_file_path).expect(&format!("Bad token file: {}", &token_file_str));
    let token_str = String::from(token_str.trim_end());
    /*
     * Process
     */
    let mut rt = tokio::runtime::Runtime::new().unwrap();
    rt.block_on(project::get_report(&token_str, &config));
}

fn process_command_line() -> (String, String) {
    let matches = App::new("asana-kanban-metrics")
        .version("0.1.0")
        .author("Parijat Mishra <parijat.mishra@gmail.com>")
        .about("Scrape Asana for Kanban Metrics")
        .arg(
            Arg::with_name("config-file")
                .short("c")
                .long("config-file")
                .takes_value(true)
                .help("path to config file"),
        )
        .arg(
            Arg::with_name("token-file")
                .short("t")
                .long("token-file")
                .takes_value(true)
                .help("path of file containing an Asana Personal Access Token"),
        )
        .get_matches();
    let config_file_str = matches
        .value_of("config-file")
        .expect("config-file must be specified");
    let token_file_str = matches
        .value_of("token-file")
        .expect("token-file must be specified");
    return (config_file_str.to_owned(), token_file_str.to_owned());
}
