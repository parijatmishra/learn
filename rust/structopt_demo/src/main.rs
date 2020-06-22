use structopt::StructOpt;

#[derive(Debug, StructOpt)]
#[structopt(about = "A demo of StructOpt")]
struct Opts {
    #[structopt(short, long, help = "Be verbose")]
    verbose: bool,

    #[structopt(short, long, name = "COUNT", help = "Run this many times")]
    optional_val: Option<i32>,

    #[structopt(short, long, name = "PORT", default_value = "8000", help = "Listen on this port")]
    default_val: i32,

    #[structopt(short, long, name = "NAME")]
    required_val: String,

}

fn main() {
    let opts = Opts::from_args();
    println!("{:?}", opts);
}
