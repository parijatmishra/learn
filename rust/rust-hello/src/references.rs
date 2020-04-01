#[allow(dead_code)]
pub fn run() {
    let mut x: i64 = 4;

    {
        // immutable reference
        let x_b1: &i64 = &x;

        // value is no longer mutable due to an immutable reference in scope
        // x += 1; // error

        // println! borrows its arguments immutably
        // value and immutable reference are immutably borrowable
        println!("x={} x_b1={}", x, *x_b1);
        // or more cleanly
        println!("x={} x_b1={}", x, x_b1);

        // value becomes mutably accessible when the compiler can infer
        // the immutable reference is no longer used
        x += 1;
    }

    {
        // mutable reference
        let x_b2: &mut i64 = &mut x;

        // value is no longer mutable due to an immutable reference in scope
        // (same as with immutable references in scope)
        // x += 1; // error

        // new:
        // value is not even *immutably* borrowable
        // because x_b2 is a mutable borrow, there cannot be any other borrows, even immutable ones
        // println!("x={}", x); // error

        // mutable reference can be read
        println!("x_b2={}", x_b2);
        // mutable reference can be mutated (after explicit deref)
        *x_b2 += 1_i64;
        println!("x_b2 after increment={}", x_b2);
    }

    // `ref`
    // another syntax for refs
    {
        // immutable reference
        let ref x_b3: i64 = x; // note: no `&` before x
                               // x_b3 is a `&i64` now
        println!("x={} x_b3={}", x, *x_b3);
        // or
        println!("x={} x_b3={}", x, x_b3);
    }

    // `mut ref`
    // another syntax for mutable refs
    {
        // mutable reference
        let ref mut x_b4: i64 = x; // note: no `&mut` before x
                                   // x_b4 is a `&mut i64` now
                                   // println!("x={}", x); // error: x has been mutably borrowed
                                   // cannot even have read access to it
        println!("x_b4={}", *x_b4);
        // or
        println!("x_b4={}", x_b4);
    }

    // use of refs in match

    // match consumes expression (unless they implement Copy)
    let y = "some strnig".to_owned();
    match y {
        n => {
            // y moved to n
            println!("n={}", n);
        }
    }
    // println!("y={}", y); // error: y moved to n

    // use ref to immutably borrow
    let y = "another string".to_owned();
    match y {
        ref n => {
            // borrow immutably
            println!("n={}", n);
            println!("y={}", y); // y is also accessible
        }
    }
    println!("y={}", y); // y is still accessible

    // use ref to mutably borrow
    let mut y = "yet another string".to_owned();
    match y {
        ref mut n => {
            *n = "mutated string".to_owned();
            // println!("y={}", y); // error: y is not accessible
            println!("n={}", n);
            // y is accessible again as n is not used below
        }
    }
    println!("y={}", y); // y is accessible again, and mutated
}
