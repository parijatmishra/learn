use std::fmt;

#[allow(dead_code)]
pub fn run() {
    // prints to console

    // simple strings
    println!("Hello from the printrs file!");

    // format string
    println!("Number: {}", 1);

    // multiple args
    // println!("Arg {} and {}.", "James"); // Won't compile: 2 positional args in format string
    // 1 actual arg
    println!("{1}, {0} {1}.", "James", "Bond"); // number of args matches

    // positional arguments in format strings
    println!(
        "{0} is from {1} and {0} likes {2}.",
        "Parijat", "Singapore", "to code 0Ol1I"
    );

    // named arguments in format strings
    println!(
        "{name} is from {place} and {name} likes {activity}.",
        name = "Parijat",
        place = "Singapore",
        activity = "to code"
    );

    // placeholder traits in format strings
    println!("10, binary:               [{:b}]", 10);
    println!("10, hex:                  [{:x}]", 10);
    println!("10, octal:                [{:o}]", 10);
    println!("10, width 5:              [{:5}]", 10);
    // width itself can be an argument
    println!("10, width {width}:              [{:width$}]", 10, width = 5);
    println!("10, fill 0s:              [{:05}]", 10);
    println!("10, left aligned:         [{:<5}]", 10);
    println!("10, right aligned:        [{:>5}]", 10);
    println!("10, right, fill -:        [{:->5}]", 10);
    println!("10, center, fill -:       [{:-^5}]", 10);

    // Debug trait
    println!("Tuple:                    {:?}", (1, "12", 'a'));
    #[derive(Debug)]
    struct Person<'a> {
        name: &'a str,
        age: u8,
    }
    let peter = Person {
        name: "Peter",
        age: 12,
    };
    println!("Person:                   {:?}", peter); // needs #[derive(Debug)]
    println!("Person:                   {:#?}", peter); // pretty debug format

    // Display trait
    #[derive(Debug)] // Debug can be derived automatically
    struct Complex {
        real: f64,
        imag: f64,
    }
    impl fmt::Display for Complex {
        fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
            write!(f, "{}{:+}i", self.real, self.imag)
        }
    }

    let complex1 = Complex {
        real: 4.3,
        imag: 2.1,
    };
    let complex2 = Complex {
        real: 4.3,
        imag: -2.1,
    };

    // Debug
    println!(
        "Complex (Debug):           1: {:?}, 2: {:?}",
        complex1, complex2
    );
    // vs Display
    println!(
        "Complex (Display):         1: {}, 2: {}",
        complex1, complex2
    );
}
