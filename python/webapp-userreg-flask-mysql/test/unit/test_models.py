from app.models import User, Role
from app import create_app, db
from nose.tools import *
import config

class TestModelRelationships:

    def setUp(self):
        config.TESTING = True
        config.SQLALCHEMY_ECHO = '1'
        self.app = create_app()
        self.app_context = self.app.app_context()
        self.app_context.push()
        self.db  = db

        self.create_data()

    def tearDown(self):
        self.delete_data()
        self.db.session.remove()
        self.app_context.pop()

    # don't put `test` anywhere in a method name if it's a fixture or helper
    def create_data(self):
        admin_role = Role(name='Admin')
        mod_role = Role(name='Moderator')
        user_role = Role(name='User')
        self.db.session.add(admin_role)
        self.db.session.add(mod_role)
        self.db.session.add(user_role)

        user_john = User(username='john', role=admin_role, email='john@example.com')
        user_susan = User(username='susan', role=user_role, email='susan@example.com')
        user_david = User(username='david', role=user_role, email='david@example.com')

        self.db.session.add(user_john)
        self.db.session.add(user_susan)
        self.db.session.add(user_david)
        self.db.session.commit()

        self.roles = [admin_role, mod_role, user_role]
        self.users = [user_john, user_susan, user_david]

    def delete_data(self):
        for user in self.users:
            db.session.delete(user)
        for role in self.roles:
            db.session.delete(role)
        db.session.commit()

    def test_query_role_users(self):
        """role.users should contain all users with that role."""
        role = Role.query.filter_by(name='User').first()
        ok_(role is not None)
        users = role.users
        ok_(set([u.username for u in users]) == set(['susan', 'david']))

    def test_user_role(self):
        """user.role should point to correct role."""
        user1 = User.query.filter_by(username='john').first()
        ok_(user1 is not None)

        role1 = user1.role
        ok_(role1 is not None)
        ok_(role1.name == 'Admin')

        user2 = User.query.filter_by(username='david').first()
        ok_(user2 is not None)

        role2 = user2.role
        ok_(role2 is not None)
        ok_(role2.name == 'User')

    # def test_user_password_setter(self):
    #     """Setting password should update password hash."""
    #     u = User(password='cat')
    #     ok_(u.password_hash is not None)

    # def test_user_no_password_getter(self):
    #     """Should not be able to read User.password."""
    #     u = User(password='cat')
    #     with assert_raises(AttributeError):
    #         u.password

    # def test_user_password_salt_is_random(self):
    #     u1 = User(password='cat')
    #     u2 = User(password='cat')
    #     ok_(u1.password_hash != u2.password_hash)

    # def test_password_verification(self):
    #     """Ensure that a set password can be checked."""
    #     u = User(password='cat')
    #     ok_(u.verify_password('cat'))
    #     assert_false(u.verify_password('dog'))
