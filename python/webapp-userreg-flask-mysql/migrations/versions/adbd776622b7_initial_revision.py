"""Initial revision

Revision ID: adbd776622b7
Revises: None
Create Date: 2016-02-19 14:46:46.380878

"""

# revision identifiers, used by Alembic.
revision = 'adbd776622b7'
down_revision = None

from alembic import op
import sqlalchemy as sa


def upgrade():
    op.create_table('roles',
        sa.Column('id', sa.Integer(), primary_key=True),
        sa.Column('name', sa.String(64), nullable=False, unique=True)
        )

    op.create_table('users',
        sa.Column('id', sa.Integer(), primary_key=True),
        sa.Column('username', sa.String(length=64), nullable=False),
        sa.Column('role_id', sa.Integer(), nullable=True),
        sa.Column('email', sa.String(length=128), nullable=False),
        sa.Column('password_hash', sa.String(128)),
        sa.Column('confirmed', sa.Boolean(), default=False),
        sa.ForeignKeyConstraint(['role_id'], ['roles.id'])
        )

    op.create_index('ix_users_username', 'users', ['username'], unique=True)

def downgrade():
    op.drop_index('ix_users_username', 'users')
    op.drop_table('users')
    op.drop_table('roles')
