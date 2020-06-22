use crate::asana::{
    AsanaClient, AsanaProject, AsanaProjectSections, AsanaProjectTaskGids, AsanaTask,
    AsanaTaskStories, AsanaUser,
};
use crate::config;
use futures::future::{join, join3, join_all};

pub async fn get_report(token: &str, config: &config::MyConfig) {
    let client = AsanaClient::new(token);

    let (asana_projects, asana_project_sections, asana_project_task_gids) =
        get_asana_data_projects(&client, config).await;

    let task_gids: Vec<_> = asana_project_task_gids
        .iter()
        .flat_map(|e| &e.task_gids)
        .collect();

    let (asana_tasks, asana_task_stories) = get_asana_data_tasks(&client, &task_gids).await;

    let user_gids: Vec<_> = asana_tasks
        .iter()
        .filter(|&t| t.assignee.is_some())
        .map(|t| &t.assignee.as_ref().unwrap().gid)
        .collect();

    let asana_users = get_asana_data_users(&client, &user_gids).await;

    do_something(
        &asana_projects,
        &asana_project_sections,
        &asana_project_task_gids,
        &asana_tasks,
        &asana_task_stories,
        &asana_users,
    );
}

async fn get_asana_data_projects<'a>(
    client: &AsanaClient<'a>,
    config: &'a config::MyConfig,
) -> (
    Vec<AsanaProject>,
    Vec<AsanaProjectSections<'a>>,
    Vec<AsanaProjectTaskGids<'a>>,
) {
    let mut project_futures = Vec::new();
    let mut project_sections_futures = Vec::new();
    let mut project_task_gids_futures = Vec::new();

    for (_, project_config) in &config.projects {
        project_futures.push(client.get_project(&project_config.gid));
        project_sections_futures.push(client.get_project_sections(&project_config.gid));
        project_task_gids_futures.push(client.get_project_task_gids(&project_config.gid));
    }

    return join3(
        join_all(project_futures),
        join_all(project_sections_futures),
        join_all(project_task_gids_futures),
    )
    .await;
}

async fn get_asana_data_tasks<'a>(
    client: &AsanaClient<'a>,
    task_gids: &'a Vec<&String>,
) -> (Vec<AsanaTask>, Vec<AsanaTaskStories<'a>>) {
    let mut task_futures = Vec::new();
    let mut task_stories_futures = Vec::new();

    for task_gid in task_gids {
        task_futures.push(client.get_task(&task_gid));
        task_stories_futures.push(client.get_task_stories(&task_gid));
    }

    return join(join_all(task_futures), join_all(task_stories_futures)).await;
}

async fn get_asana_data_users<'a>(
    client: &AsanaClient<'a>,
    user_gids: &'a Vec<&String>,
) -> Vec<AsanaUser> {
    let mut user_futures = Vec::new();

    for user_gid in user_gids {
        user_futures.push(client.get_user(&user_gid));
    }

    return join_all(user_futures).await;
}
// ------

fn do_something(
    arg1: &Vec<AsanaProject>,
    arg2: &Vec<AsanaProjectSections>,
    arg3: &Vec<AsanaProjectTaskGids>,
    arg4: &Vec<AsanaTask>,
    arg5: &Vec<AsanaTaskStories>,
    arg6: &Vec<AsanaUser>,
) {
    println!("\n\n\n** Projects:\n{:?}", arg1);
    println!("\n\n\n** Sections:\n{:?}", arg2);
    println!("\n\n\n** Task GIDs:\n{:?}", arg3);
    println!("\n\n\n** Tasks:\n{:?}", arg4);
    println!("\n\n\n** Stories:\n{:?}", arg5);
    println!("\n\n\n** Users:\n{:?}", arg6);
}
