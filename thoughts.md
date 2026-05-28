My first question was: how to manage the database.
this is the option I see right now

         node (Main topic)
          /\
    branch branch (for example more specific topic related to main one/question etc)
       /\     /\
    leaves  leaves (comments/reactions/opinions on branches)

tables:
1. node (id, title)
2. branch (id, node_id, author_id, title, description, comments_count, rating, tags, original_photo_url, created_at, updated at)
3. leaves (id, branch_id, author_id, commentary, likes, created_at, updated_at)