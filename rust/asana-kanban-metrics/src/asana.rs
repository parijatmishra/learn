use crate::asana::AsanaError::Missing;
use hyper::body::HttpBody;
use hyper::client::connect::dns::GaiResolver;
use hyper::client::HttpConnector;
use hyper::{header, Body, Method, Request, Response, Uri};
use hyper_tls::HttpsConnector;
use log;
use serde::Deserialize;
use serde_json;
use std::collections::HashMap;
use std::fmt;

// ------

static BASE_URL: &str = "https://app.asana.com/api/1.0";

pub struct AsanaClient<'a> {
    client: hyper::Client<HttpsConnector<HttpConnector<GaiResolver>>>,
    token: &'a str,
}

#[derive(Debug, Deserialize)]
pub struct AsanaEmbeddedObject {
    pub gid: String,
    pub name: Option<String>,
}

#[derive(Debug, Deserialize)]
pub struct AsanaProject {
    pub gid: String,
    pub name: String,
    pub created_at: chrono::DateTime<chrono::Utc>,
}

#[derive(Debug)]
pub struct AsanaProjectSections<'a> {
    pub project_gid: &'a str,
    pub sections: Vec<AsanaEmbeddedObject>,
}

#[derive(Debug)]
pub struct AsanaProjectTaskGids<'a> {
    pub project_gid: &'a str,
    pub task_gids: Vec<String>,
}

#[derive(Debug, Deserialize)]
pub struct AsanaTask {
    pub gid: String,
    pub name: String,
    pub created_at: String,
    pub completed: bool,
    pub completed_at: Option<chrono::DateTime<chrono::Utc>>,
    pub assignee: Option<AsanaEmbeddedObject>,
    pub memberships: Vec<HashMap<String, AsanaEmbeddedObject>>,
}

#[derive(Debug, Deserialize)]
pub struct AsanaStory {
    pub created_at: chrono::DateTime<chrono::Utc>,
    pub resource_subtype: String,
    pub text: String,
}

#[derive(Debug)]
pub struct AsanaTaskStories<'a> {
    pub task_gid: &'a str,
    pub stories: Vec<AsanaStory>,
}

#[derive(Debug, Deserialize)]
pub struct AsanaUser {
    pub gid: String,
    pub name: String,
    pub email: String,
}

impl AsanaUser {
    fn missing_user(user_gid: &str) -> AsanaUser {
        Self {
            gid: String::from(user_gid),
            name: format!("MissingUser({})", user_gid),
            email: format!("{}@nowhere.com", user_gid),
        }
    }
}
// ------ Internal helper structs

#[derive(Debug, Deserialize)]
struct AsanaContainer<T> {
    data: T,
}

#[derive(Debug, Deserialize)]
struct AsanaPage<T> {
    data: Vec<T>,
    next_page: Option<AsanaNextPage>,
}

#[derive(Debug, Deserialize)]
struct AsanaNextPage {
    offset: String,
}

// ------
#[derive(Debug)]
enum AsanaError {
    Missing,
}

impl fmt::Display for AsanaError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{:?}", self)
    }
}

impl std::error::Error for AsanaError {}

impl<'a> AsanaClient<'a> {
    pub fn new(token: &str) -> AsanaClient {
        let https = hyper_tls::HttpsConnector::new();
        let client = hyper::Client::builder().build::<_, hyper::Body>(https);
        AsanaClient { client, token }
    }

    pub async fn get_project(&self, project_gid: &str) -> AsanaProject {
        let uri_str = format!(
            "{}/projects/{}?opt_fields=this.name,this.created_at",
            BASE_URL, project_gid
        );
        log::debug!("get_project: project={}", project_gid);
        let body_str = self.get_response_as_string(&uri_str).await.unwrap();
        let project: AsanaContainer<AsanaProject> =
            serde_json::from_str(&body_str).unwrap_or_else(|err| {
                panic!(
                    "get_project: Could not parse AsanaProject: uri={} response.body={} error={}",
                    uri_str, body_str, err
                );
            });
        let project = project.data;
        return project;
    }

    pub async fn get_project_sections(&self, project_gid: &'a str) -> AsanaProjectSections<'a> {
        let mut sections: Vec<AsanaEmbeddedObject> = Vec::with_capacity(10 as usize);
        let mut offset = None;
        loop {
            let uri_str = match offset {
                None => format!("{}/projects/{}/sections?limit=100", BASE_URL, project_gid),
                Some(offset) => format!(
                    "{}/projects/{}/sections?limit=100&offset={}",
                    BASE_URL, project_gid, offset
                ),
            };

            log::debug!("get_project_sections: project={}", project_gid);
            let body_str = self.get_response_as_string(&uri_str).await.unwrap();
            let page: AsanaPage<AsanaEmbeddedObject> =
                serde_json::from_str(&body_str).unwrap_or_else(|err| {
                    panic!(
                        "get_project_sections: Could not parse page: uri={} response.body={} error={}",
                        uri_str,
                        body_str,
                        err
                    );
                });
            for section in page.data {
                sections.push(section);
            }
            offset = page.next_page.map(|np| np.offset);
            if offset.is_none() {
                break;
            }
        }
        return AsanaProjectSections {
            project_gid,
            sections,
        };
    }

    pub async fn get_project_task_gids(&self, project_gid: &'a str) -> AsanaProjectTaskGids<'a> {
        let mut task_gids: Vec<String> = Vec::with_capacity(100 as usize);
        let mut offset = None;
        loop {
            let uri_str = match offset {
                None => format!(
                    "{}/projects/{}/tasks?opt_fields=this.gid&limit=100",
                    BASE_URL, project_gid
                ),
                Some(offset) => format!(
                    "{}/projects/{}/tasks?opt_fields=this.gid&limit=100&offset={}",
                    BASE_URL, project_gid, offset
                ),
            };
            log::debug!("get_project_task_gids: project={}", project_gid);
            let body_str = self.get_response_as_string(&uri_str).await.unwrap();
            let page: AsanaPage<AsanaEmbeddedObject> =
                serde_json::from_str(&body_str).unwrap_or_else(|err| {
                    panic!(
                        "get_project_task_gids: Could not parse page: uri={} response.body={} error={}",
                        uri_str,
                        body_str,
                        err
                    );
                });
            for task in page.data {
                task_gids.push(task.gid);
            }
            offset = page.next_page.map(|np| np.offset);
            if offset.is_none() {
                break;
            }
        }
        return AsanaProjectTaskGids {
            project_gid,
            task_gids,
        };
    }

    pub async fn get_task(&self, task_gid: &str) -> AsanaTask {
        let opt_fields = "this.(name|created_at|completed|completed_at),this.assignee.gid,this.memberships.section.gid";
        let uri_str = format!("{}/tasks/{}?opt_fields={}", BASE_URL, task_gid, opt_fields);

        log::debug!("get_task: task={}", task_gid);
        let body_str = self.get_response_as_string(&uri_str).await.unwrap();
        let task: AsanaContainer<AsanaTask> =
            serde_json::from_str(&body_str).unwrap_or_else(|err| {
                panic!(
                    "get_task: Could not parse task: uri={} response.body={} error={}",
                    uri_str, body_str, err
                );
            });
        let task = task.data;
        return task;
    }

    pub async fn get_task_stories(&self, task_gid: &'a str) -> AsanaTaskStories<'a> {
        let mut stories = Vec::new();
        let opt_fields = "this.(created_at|resource_subtype|text)";
        let mut offset = None;
        loop {
            let uri_str = match offset {
                None => format!(
                    "{}/tasks/{}/stories?opt_fields={}&limit=100",
                    BASE_URL, task_gid, opt_fields
                ),
                Some(offset) => format!(
                    "{}/tasks/{}/stories?opt_fields={}&limit=100&offset={}",
                    BASE_URL, task_gid, opt_fields, offset
                ),
            };

            log::debug!("get_task_stories: task={}", task_gid);
            let body_str = self.get_response_as_string(&uri_str).await.unwrap();

            let page: AsanaPage<AsanaStory> =
                serde_json::from_str(&body_str).unwrap_or_else(|err| {
                    panic!(
                        "get_task_stories: Could not parse page: uri={} response.body={} error={}",
                        uri_str, body_str, err
                    );
                });
            for story in page.data {
                stories.push(story);
            }
            offset = page.next_page.map(|np| np.offset);
            if offset.is_none() {
                break;
            }
        }
        return AsanaTaskStories { task_gid, stories };
    }

    pub async fn get_user(&self, user_gid: &str) -> AsanaUser {
        let uri_str = format!(
            "{}/users/{}?opt_fields=this.(name|email)",
            BASE_URL, user_gid
        );

        log::debug!("get_user: user_gid={}", user_gid);
        match self.get_response_as_string(&uri_str).await {
            Ok(body_str) => {
                let user: AsanaContainer<AsanaUser> = serde_json::from_str(&body_str)
                    .unwrap_or_else(|err| {
                        panic!(
                            "get_user: Could not parse user: uri={} response.body={} error={}",
                            uri_str, body_str, err
                        );
                    });
                return user.data;
            }
            Err(Missing) => AsanaUser::missing_user(user_gid),
        }
    }

    async fn get_response_as_string(&self, uri_str: &str) -> Result<String, AsanaError> {
        let uri = uri_str.parse::<Uri>().expect("URL parsing error");
        let auth_header_val_str = format!("Bearer {}", self.token);
        let request = Request::builder()
            .method(Method::GET)
            .uri(uri)
            .header(header::AUTHORIZATION, &auth_header_val_str)
            .body(Body::empty())
            .expect("Request Creation Error");
        let mut response = self.client.request(request).await.expect("HTTP GET error");

        let length = Self::get_content_length(&uri_str, &response);
        log::debug!(
            "get_response_as_string: uri={} status={} content-length={:?}",
            uri_str,
            response.status(),
            length
        );

        if response.status().eq(&hyper::StatusCode::NOT_FOUND) {
            return Err(AsanaError::Missing);
        }

        let mut bytes: Vec<u8> = Vec::with_capacity(length.unwrap_or(1024) as usize);
        while let Some(chunk) = response.body_mut().data().await {
            bytes.extend(chunk.expect("Chunk should have bytes"));
        }
        let body_str = String::from_utf8(bytes).expect("Body should be UTF-8 string");

        if !response.status().is_success() {
            panic!(
                "get_response_as_string: bad response: uri={}\n\t- response={:?}\n\t- body={:?}",
                uri_str, response, body_str
            );
        }

        return Ok(body_str);
    }

    fn get_content_length(uri_str: &str, response: &Response<Body>) -> Option<u32> {
        let length: Option<u32> = response.headers().get(header::CONTENT_LENGTH).map(|h| {
            h.to_str()
                .unwrap_or_else(|err| {
                    panic!(
                        "get_response_as_string: content-length non-string: uri={} response={:?} error={}",
                        uri_str,
                        response,
                        err
                    );
                })
                .parse()
                .unwrap_or_else(|err| {
                    panic!(
                        "get_response_as_string: content-length not integer: uri={} response={:?} error={}",
                        uri_str,
                        response,
                        err
                    );
                })
        });
        return length;
    }
}
