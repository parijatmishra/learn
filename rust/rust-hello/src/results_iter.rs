use std::num::ParseIntError;

pub fn run() {
    let strings: Vec<&str> = vec!["93", "tofu", "18"];

    // How to consume an Iterator<Item = Result<_, _>>

    // Option 1: All or nothing with .collect() on Iterator
    // Iterator<Result<Vec<_>, Err>> -> Result<Vev<_>, Err>

    // x1, x2: Iterator<Result<_, _>>
    let x = strings.iter().map(|s| s.parse::<i32>());
    let ret: Result<Vec<i32>, _> = x.collect();
    println!("collect: ret = {:?}", ret); // Result::Err<ParseIntError>

    // Option 2: ignore failures, collect successes with .filter_map on Iterator

    // x1, x2: Iterator<Result<_, _>>
    let x = strings.iter().map(|s| s.parse::<i32>());
    let ret: Vec<i32> = x.filter_map(Result::ok).collect();
    println!("filter_map: ret = {:?}", ret); // Result::Ok<Vec<_>>

    // Option 3: collect successes and failures with .partition on Iterator
    // x1, x2: Iterator<Result<_, _>>
    let x = strings.iter().map(|s| s.parse::<i32>());
    let (numbers, errors): (Vec<Result<i32, _>>, Vec<Result<i32, _>>) = x.partition(Result::is_ok);
    println!("partition: a, b = {:?}, {:?}", numbers, errors);

    // instead of Vec<Result<i32, _>>, we want Vec<i32> and Vec<Err>
    let numbers: Vec<i32> = numbers.into_iter().map(Result::unwrap).collect();
    let errors: Vec<ParseIntError> = errors.into_iter().map(Result::unwrap_err).collect();
    println!("numbers = {:?}", numbers);
    println!("errors = {:?}", errors);
}
