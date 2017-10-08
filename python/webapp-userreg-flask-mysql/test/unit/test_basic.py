# Test that the app can start
import re
from flask import url_for
from nose.tools import *
import config

class TestAppStart:

    def setUp(self):
        config.TESTING = True
        config.SERVER_NAME = 'localhost'
        from app import create_app
        self.app = create_app()
        self.app_context = self.app.app_context()
        self.app_context.push()
        self.client = self.app.test_client()

    def tearDown(self):
        self.app_context.pop()

    def test_app_exists(self):
        from flask import current_app
        assert_false(current_app is None)

    def test_index(self):
        response = self.client.get(url_for('main.index'))
        assert_true(re.compile('Hello,\s+Stranger\s+!').search(response.get_data(as_text=True)))

    def test_not_found(self):
        response = self.client.get('/nosuchpage')
        response_data = response.get_data(as_text=True)
        assert_true('Blog :: Page Not Found' in response_data)
