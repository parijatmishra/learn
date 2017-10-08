#!/usr/bin/env python
from app import create_app
from flask.ext.script import Manager
from flask.ext.migrate import Migrate, MigrateCommand

app = create_app()
from app import db
manager = Manager(app)
migrate = Migrate(app, db)
manager.add_command('db', MigrateCommand)

@manager.command
def drop_tables():
    """Convenience command to drop all tables in the database.  Useful
    if your tests or migrations got the database into an awkward state
    and you want to start again.

    Needs SQLALCHEMY_DATABASE_URI environment variable to be set.
    """
    import sqlalchemy as sa
    import os
    dburi = os.environ['SQLALCHEMY_DATABASE_URI']
    print "Dropping tables in database {}".format(dburi)
    engine = sa.create_engine(dburi)
    conn = engine.connect()
    trans = conn.begin()
    inspector = sa.engine.reflection.Inspector.from_engine(engine)
    metadata = sa.schema.MetaData()
    tables = []
    all_foreign_keys = []
    # gather data
    print "Inspecting"
    for table_name in inspector.get_table_names():
        print "Table {}".format(table_name)
        local_fks = []
        for fk in inspector.get_foreign_keys(table_name):
            if not fk['name']:
                continue
            print "\tForeign Key {}: {} -> {}.{}".format(fk['name'], fk['constrained_columns'], fk['referred_table'], fk['referred_columns'])
            local_fks.append(sa.schema.ForeignKeyConstraint((), (), name=fk['name']))

        t = sa.schema.Table(table_name, metadata, *local_fks)
        tables.append(t)

        all_foreign_keys.extend(local_fks)

    for fkc in all_foreign_keys:
        print "Dropping Foreign Key Constraint {} on table {}".format(fkc.name, fkc.table.name)
        conn.execute(sa.schema.DropConstraint(fkc))
    for table in tables:
        print "Dropping Table {}".format(table)
        conn.execute(sa.schema.DropTable(table))

@manager.command
def create_dev_database():
    """
    Use this when running a development server.  It creates tables without
    using db migration scripts, instead asking SQLAlchemy to create them
    directly from your models.  Good for testing/iterating.  When done,
    write proper db migration scripts and test.
    """
    from app import db
    from app.models import Role, User
    db.drop_all()
    db.create_all()

    admin_role = Role(name='Admin')
    mod_role = Role(name='Moderator')
    user_role = Role(name='User')
    db.session.add(admin_role)
    db.session.add(mod_role)
    db.session.add(user_role)

    user_john = User(username='john', role=admin_role, password='cat', email='john@example.com', confirmed=True)
    user_susan = User(username='susan', role=user_role, password='cat', email='susan@example.com', confirmed=False)
    user_david = User(username='david', role=user_role, password='cat', email='david@example.com', confirmed=False)

    db.session.add(user_john)
    db.session.add(user_susan)
    db.session.add(user_david)
    db.session.commit()

if __name__ == "__main__":
    manager.run()
