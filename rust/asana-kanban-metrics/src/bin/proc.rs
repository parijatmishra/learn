use metrics::config::*;

use clap::{App, Arg};
use env_logger;
use std::fs;
use std::path::Path;

fn main() {
    /* Logging */
    env_logger::init();

    /* Command Line */
    let (config_file_str, _input_file_str) = process_command_line();

    /*
     * Config data
     */
    let config_file_path = Path::new(&config_file_str)
        .canonicalize()
        .expect(&format!("Bad config file path: {}", &config_file_str));
    let config_str = fs::read_to_string(config_file_path)
        .expect(&format!("Bad config file: {}", &config_file_str));
    let config: MyConfig = parse_config(&config_str);

    println!("Config: {:?}", config);

    /*
     * Input file -- output of `fetch` program
     */
    // let input_file_path: PathBuf = Path::new(&input_file_str)
    //     .canonicalize()
    //     .expect(&format!("Bad input file path: {}", &input_file_str));
    // let input_str =
    //     fs::read_to_string(input_file_path).expect(&format!("Bad token file: {}", &input_file_str));
    // let output: Output = serde_json::from_str(&input_str).expect("Invalid output.");
    //
    /*
     * Process
     */
    // println!("{:#?}", proc_data(&config, &output));
}

fn process_command_line() -> (String, String) {
    let matches = App::new("proc")
        .version("0.1.0")
        .author("Parijat Mishra <parijat.mishra@gmail.com>")
        .about("Process Output of `fetch`")
        .arg(
            Arg::with_name("config-file")
                .short("c")
                .long("config-file")
                .takes_value(true)
                .help("path to config file"),
        )
        .arg(
            Arg::with_name("input-file")
                .short("i")
                .long("input-file")
                .takes_value(true)
                .help("path of file containing the output of the `fetch` program."),
        )
        .get_matches();
    let config_file_str = matches
        .value_of("config-file")
        .expect("config-file must be specified");
    let input_file_str = matches
        .value_of("input-file")
        .expect("input-file must be specified");
    return (config_file_str.to_owned(), input_file_str.to_owned());
}

// struct Event<'a> {
//     at: DateTime<Utc>,
//     what: EventType<'a>,
// }
//
// enum EventType<'a> {
//     TaskCreated {
//         task_id: &'a str,
//     },
//     TaskCompleted {
//         task_id: &'a str,
//     },
//     TaskStateChanged {
//         task_id: &'a str,
//         section_id: &'a str,
//     },
// }
//
// struct Index<'a> {}
//
// impl<'a> Index<'a> {
//     fn new() -> Self {
//         Index {}
//     }
// }
//
// fn create_index<'a>(output: &'a Output) -> Index<'a> {
//     let mut index = Index::new();
//     return index;
// }

// #[derive(Debug)]
// struct Report<'a> {
//     projects: Vec<ReportProject<'a>>,
// }
//
// #[derive(Debug)]
// struct ReportProject<'a> {
//     name: &'a str,
//     cfd: Vec<ReportPeriodCount>,
// }
//
// #[derive(Debug)]
// struct ReportPeriodCount {
//     start: DateTime<Utc>,
//     count: u32,
// }
//
// fn report_period_counts<'a>(
//     states: &Vec<String>,
//     sections: &'a Vec<Section>,
// ) -> Vec<ReportPeriodCount> {
//     return Vec::new();
// }
//
// fn proc_data<'a>(config: &MyConfig, output: &'a Output) -> Report<'a> {
//     let report = Report {
//         projects: output
//             .projects
//             .iter()
//             .map(|p| ReportProject {
//                 name: &p.name,
//                 cfd: report_period_counts(
//                     &config
//                         .projects
//                         .get(&p.gid)
//                         .expect("Should have project gid")
//                         .cfd_states,
//                     &p.sections,
//                 ),
//             })
//             .collect(),
//     };
//     println!("{:?}", output.created_at);
//
//     return report;
// }
