use actix_web::get;
use actix_web::{web, App, HttpResponse, HttpServer, Responder};
use std::sync::Mutex;

struct AppData {
    counter: Mutex<i32>,
}

fn index() -> impl Responder {
    HttpResponse::Ok().body("Hello, World!")
}

// alternative
// using macro attributes to define routes
#[get("/hello")]
fn index2() -> impl Responder {
    HttpResponse::Ok().body("Hey there!")
}

fn index3(data: web::Data<AppData>) -> impl Responder {
    let mut counter = data.counter.lock().unwrap(); // get counter's MutexGuard
    *counter += 1;

    HttpResponse::Ok().body(format!("Hello, again! You've called me {} times", counter))
}

fn main() {
    let counter = web::Data::new(AppData {
        counter: Mutex::new(0),
    });

    HttpServer::new(move || {
        // move needed because we are using 'counter'
        App::new()
            .register_data(counter.clone()) // send a clone to the new App instace
            .route("/", web::get().to(index))
            .service(index2) // alternative - with macro defined "service"
            .route("/again", web::get().to(index3))
    })
    .bind("127.0.0.1:8088")
    .unwrap()
    .run()
    .unwrap();
}
