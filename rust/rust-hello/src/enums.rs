#[allow(dead_code)]
enum List {
    /// The empty list.
    Nil,
    /// A new list, consisting of an element and an existing list.
    Cons(u32, Box<List>),
}

impl List {
    fn new() -> List {
        List::Nil
    }

    fn prepend(self, elem: u32) -> List {
        Self::Cons(elem, Box::new(self))
    }

    fn len(&self) -> u32 {
        match self {
            List::Nil => 0,
            List::Cons(_, ref tail) => 1 + tail.len(),
        }
    }

    fn stringify(&self) -> String {
        match self {
            List::Nil => format!("Nil"),
            List::Cons(head, ref tail) => format!("{}, {}", head, tail.stringify()),
        }
    }
}

#[allow(dead_code)]
pub fn run() {
    let list = List::new();

    let list = list.prepend(1);
    let list = list.prepend(2);
    let list = list.prepend(3);

    println!("Length: {}", list.len());
    println!("List: {}", list.stringify());
}
