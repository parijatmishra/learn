import re
from flask import url_for
from app import create_app, db
from app.models import User
from nose.tools import *
from app import mail
import config

class TestAuthentication:

    def setUp(self):
        config.SERVER_NAME = 'localhost'
        config.WTF_CSRF_ENABLED = False
        config.TESTING = True
        self.app = create_app()
        self.app_context = self.app.app_context()
        self.app_context.push()        
        self.client = self.app.test_client(use_cookies=True)
        self.db = db

        # create dummy user
        u = User(email='john@example.com', username='john', password='cat', confirmed=True)
        self.db.session.add(u)
        self.db.session.commit()

    def tearDown(self):
        # Delete dummy user
        u = User.query.filter_by(email='john@example.com').first()
        self.db.session.delete(u)
        self.db.session.commit()

        self.db.session.remove()
        self.app_context.pop()

    def login(self, login, password):
        return self.client.post(url_for('auth.login'),
            data=dict(email=login, password=password),
            follow_redirects=True)

    def test_auth_registeration_login_logout(self):
        # Check that Sign In and Register Links are present in the main page
        response = self.client.get(url_for('main.index'))
        data = response.get_data(as_text=True)
        assert_true("Sign In" in data)
        assert_true("Register" in data)

        # Check that the Registration page has the form
        response = self.client.get(url_for("auth.register"))
        data = response.get_data(as_text=True)
        assert_true('Register' in data)
        assert_true('Email' in data)
        assert_true('Username' in data)
        assert_true('Password' in data)
        assert_true('Confirm Password' in data)

        # Register - should redirect to main page
        with mail.record_messages() as outbox:
            response = self.client.post(url_for('auth.register'), data={
                "email": "sam@example.com",
                "username": "sam",
                "password": "cat",
                "password2": "cat"
                }, follow_redirects=True)
            data = response.get_data(as_text=True)
            assert_true("A confirmation email has been sent to your email address."
                in data)
            assert_true(len(outbox) == 1) # Mail was sent
            assert_true('Confirm Your Account' in outbox[0].subject)

        # Login page contains the form
        response = self.client.get(url_for('auth.login'))
        data = response.get_data(as_text=True)
        assert_true('Login' in data)
        assert_true('Email' in data)
        assert_true('Password' in data)
        assert_true('Keep me logged in' in data)
        assert_true('Log In' in data)

        # Incorrect logins return error
        response = self.login('foo@example.com', 'badpassword')
        assert_true('Invalid email or password' in response.get_data(as_text=True))

        response = self.login('sam@example.com', 'badpassword')
        assert_true('Invalid email or password' in response.get_data(as_text=True))

        # Correct login
        response = self.login('sam@example.com', 'cat')
        data = response.get_data(as_text=True)
        assert_true('You were logged in' in data)
        assert_true(re.compile("Hello,\s+sam").search(data))
        # Should prompt the user to confirm their email address
        assert_true("You have not confirmed your account yet" in data)

        # Resend confirmation
        with mail.record_messages() as outbox:
            response = self.client.get(url_for('auth.resend_confirmation'), follow_redirects=True)
            data = response.get_data(as_text=True)
            assert_true('A new confirmation email has been sent to you' in data)
            assert_true(len(outbox) == 1)

        # Confirm account
        user = User.query.filter_by(email='sam@example.com').first()

        # Attempt to use another user's token rejected
        token = User.query.filter_by(email='john@example.com').first().generate_confirmation_token()
        response = self.client.get(url_for('auth.confirm', token=token),
            follow_redirects=True)
        data = response.get_data(as_text=True)
        assert_true(re.compile('The confirmation link is invalid or has expired.').search(data))

        # Incorrect token rejected
        token = user.generate_confirmation_token()
        response = self.client.get(url_for('auth.confirm', token=' ' + token),
            follow_redirects=True)
        data = response.get_data(as_text=True)
        assert_true(re.compile('The confirmation link is invalid or has expired.').search(data))

        # Correct token accepted
        response = self.client.get(url_for('auth.confirm', token=token),
            follow_redirects=True)
        data = response.get_data(as_text=True)
        assert_true("You have confirmed your account." in data)

        # Main page should have Sign Out link, and welcome us by username
        response = self.client.get(url_for('main.index'))
        data = response.get_data(as_text=True)
        assert_true(re.compile("Hello,\s+sam").search(data))
        assert_true("Sign Out" in data)

        # Check logout - should redirect to main page
        response = self.client.get(url_for('auth.logout'), follow_redirects=False)
        assert_true(response.status_code == 302)

        # Should have Sign In link on main page now
        response = self.client.get(url_for('main.index'))
        assert_true("Sign In" in response.get_data(as_text=True))

        # Delete test user
        User.query.filter_by(email='sam@example.com').delete()


    def test_confirmed_user_not_asked_reconfirm(self):
        response = self.login('john@example.com', 'cat')
        assert_true('You were logged in' in response.get_data(as_text=True))

        response = self.client.get(url_for('auth.unconfirmed'))
        assert_true(response.status_code == 302)

    def test_confirm_already_confirmed(self):
        response = self.login('john@example.com', 'cat')
        assert_true('You were logged in' in response.get_data(as_text=True))

        user = User.query.filter_by(email='john@example.com').first()
        token = user.generate_confirmation_token()
        response = self.client.get(url_for('auth.confirm', token=token), follow_redirects=True)
        assert_true(re.compile('Hello,\s+john').search(response.get_data(as_text=True)))
