use std::mem;

fn apply_fn<F>(f: F)
where
    F: Fn(),
{
    println!("=== apply_fn start ==================");
    println!("--- 1. -----------------------------");
    f();
    println!("--- 2. -----------------------------");
    f();
    println!("=== apply_fn end   ==================");
}

fn apply_fn_mut<F>(mut f: F)
where
    F: FnMut(),
{
    println!("=== apply_fn_mut start ===============");
    println!("--- 1. -----------------------------");
    f();
    println!("--- 2. -----------------------------");
    f();
    println!("=== apply_fn_mut end   ===============");
}

fn apply_fn_once<F>(f: F)
where
    F: FnOnce(),
{
    println!("=== apply_fn_once start ==============");
    println!("--- 1. -----------------------------");
    f();
    println!("=== apply_fn_once end   ==============");
}

fn apply_with_args<F>(arg: i32, operate: F) -> i32
where
    F: Fn(i32) -> i32,
{
    operate(arg)
}

#[allow(dead_code)]
pub fn run() {
    let greeting = "Hello";
    let farewell = "Goodbye".to_owned();
    let count = 1;

    // This closure captures variables as &T
    // It can be passed to a function that accepts Fn(), FnMut() or FnOnce()
    let journal1 = || {
        println!("I said, {}", greeting); // &str
        println!("I said, {}, {} times", farewell, count); // &String
        println!("Now I will sleep.");
    };

    apply_fn(journal1);
    apply_fn_mut(journal1);
    apply_fn_once(journal1);

    let greeting = "Hello";
    let mut farewell = "Goodbye".to_owned();
    let mut count = 1;

    // This closure captures variables as &mut T
    // It can be passed to a function that accept FnMut() or FnOnce()
    let journal2 = || {
        println!("I said, {}", greeting); // &str
        count += 1;
        println!("I said, {}, {} times", farewell, count); // &String
        farewell.push_str("!!!"); // &mut String
        count += 1;
        println!("I screamed, {}, {} times", farewell, count);
        println!("Now I will sleep.");
    };

    // apply_fn(journal2); // error
    apply_fn_mut(journal2);
    // apply_fn_once(journal2); // error: journal2 itself is moved into apply_fn_mut
    // cannot pass to apply_fn_once again

    let greeting = "Hello";
    let mut farewell = "Goodbye".to_owned();
    let mut count = 1;

    // This closure moves ownership of a variable
    // It can be passed to a function that accepts FnOnce()
    let journal3 = move || {
        println!("I said, {}", greeting); // &str
        count += 1;
        println!("I said, {}, {} times", farewell, count); // &String
        farewell.push_str("!!!"); // &mut String
        count += 1;
        println!("I screamed, {}, {} times", farewell, count);
        mem::drop(farewell); // String (move)
        println!("Now I will sleep.");
    };

    // apply_fn(journal3); // error: requires closure type to be Fn
    // apply_fn_mut(journal3); // error: requires closure type to FnMut
    apply_fn_once(journal3);

    // a closure that accepts an argument and returns a value
    // passed immediately to another function that accepts an Fn(i32) -> i32
    let m = apply_with_args(3, |i| {
        let j = i * 2;
        let k = j + 1;
        let l = k * 10 + j;
        l
    });
    assert_eq!(m, 76);

    // A function that returns an Fn
    // must use `move` on captured variables to be able to hold on to them
    fn create_multiplier(i: i32) -> impl Fn(i32) -> i32 {
        // this is the closure that is returned
        move |x| i * x // closure takes ownership of i
    }

    println!("=== doubler start ==================");
    let doubler = create_multiplier(2);
    // The Fn can be called multiple times
    println!("--- 1. -----------------------------");
    println!("double(3) == {}", doubler(3));
    println!("--- 2. -----------------------------");
    println!("double(4) == {}", doubler(4));
    println!("=== doubler end   ==================");

    println!("=== create_obsessive start ==================");
    // A function that returns an FnMut
    fn create_obsessive() -> impl FnMut(i32) -> String {
        let mut v: Vec<i32> = Vec::new();
        // this is the closure that is returned
        move |i: i32| {
            v.push(i); // the closure takes ownership of v
            format!("{:?}", v).to_owned()
        }
    }

    // Create two FnMut instances
    let mut obsessive1 = create_obsessive();
    let mut obsessive2 = create_obsessive();

    // Each FnMut remembers its own history
    println!("--- 1. -----------------------------");
    println!("obsessive1={}", obsessive1(1));
    println!("obsessive2={}", obsessive2(10));
    println!("--- 2. -----------------------------");
    println!("obsessive1={}", obsessive1(2));
    println!("obsessive2={}", obsessive2(20));
    println!("--- 3. -----------------------------");
    println!("obsessive1={}", obsessive1(3));
    println!("obsessive2={}", obsessive2(30));
    println!("=== create_obsessive end   ==================");
}
