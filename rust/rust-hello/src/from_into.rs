use std::convert::From;

#[derive(Debug)]
struct MyNumber {
    value: i32,
}

impl From<i32> for MyNumber {
    fn from(x: i32) -> Self {
        MyNumber { value: x }
    }
}

#[allow(dead_code)]
pub fn run() {
    let primitive = 10_i32;

    let mynum1 = MyNumber::from(primitive);

    // type annotation required
    let mynum2: MyNumber = primitive.into();

    println!(
        "primitive: {}, mynum1: {:?}, mynum2: {:?}",
        primitive, mynum1, mynum2,
    );
}
