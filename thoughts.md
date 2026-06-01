My first question was: how to manage the database.
this is the option I see right now

         root (Main topic)
          /\
    branches branches (more specific topics/questions related to the main one)
       /\       /\
    leaves    leaves (comments/reactions/opinions on branches)

tables:
1. root (id, title, description, activity_rating, created_at, updated_at)
2. branch (id, root_id, author_id, title, description, comments_count, rating, tags, original_photo_url, created_at, updated_at)
3. leaves (id, branch_id, author_id, commentary, likes, created_at, updated_at)