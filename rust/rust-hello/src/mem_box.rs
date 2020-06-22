use std::mem;

#[allow(dead_code)]
#[derive(Debug, Clone, Copy)]
struct Point {
    x: f64,
    y: f64
}

#[allow(dead_code)]
#[derive(Debug, Clone, Copy)]
struct Rectangle {
    top_left: Point,
    bottom_right: Point
}

#[allow(dead_code)]
fn origin() -> Point {
    Point { x: 0.0, y: 0.0 }
}

#[allow(dead_code)]
fn boxed_origin() -> Box<Point> {
    Box::new(Point { x: 0.0, y: 0.0 })
}

pub fn run() {
    // Stack
    let p = origin();
    println!("size_of::<Point>()            = {}", mem::size_of::<Point>());
    println!("size_of_val(p)                = {}", mem::size_of_val(&p));
    let r = Rectangle {
        top_left: origin(),
        bottom_right: Point {x: 1., y: 2.}
    };
    println!("size_of::<Rectangle>()        = {}", mem::size_of::<Rectangle>());
    println!("size_of_val(r)                = {}", mem::size_of_val(&r));

    // Heap
    let bp = boxed_origin();
    println!("size_of::<Box<Point>>()       = {}", mem::size_of::<Box<Point>>());
    println!("size_of_val(bp)               = {}", mem::size_of_val(&bp));
    let br = Box::new(Rectangle {
        top_left: origin(),
        bottom_right: Point {x: 1., y: 2.}
    });
    println!("size_of::<Box<Rectangle>>()   = {}", mem::size_of::<Box<Rectangle>>());
    println!("size_of_val(br)               = {}", mem::size_of_val(&br));

    // Can unbox, or dereference back to stack
    let up = *bp;
    println!("size_of_val(&up)              = {}", mem::size_of_val(&up));
    let ur = *br;
    println!("size_of_val(&ur)              = {}", mem::size_of_val(&ur));
}