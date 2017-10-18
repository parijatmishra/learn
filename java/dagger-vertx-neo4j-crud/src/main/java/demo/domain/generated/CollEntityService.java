package demo.domain.generated;

import lib.db.Page;
import lib.exc.Api404MissingResourceException;
import lib.neo4j.Neo4JHelper;
import lib.web.LinkRegistry;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 *
 */
public class CollEntityService
{
    private static final Logger LOG = LoggerFactory.getLogger("svc.CollEntity");

    private final Driver driver;
    private final LinkRegistry linkRegistry;
    private final CollEntityRepository collEntityRepository;

    @Inject
    public CollEntityService(final Driver driver,
                             final LinkRegistry linkRegistry,
                             final CollEntityRepository collEntityRepository)
    {
        this.driver = driver;
        this.linkRegistry = linkRegistry;
        this.collEntityRepository = collEntityRepository;
    }

    public void init()
    {
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // ID is unique
                Neo4JHelper.createUniqueConstraint(_transaction, "CollEntity", "_id", LOG);

                // @Searchable @Required private String name
                Neo4JHelper.createIndex(_transaction, "CollEntity", "name", LOG);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    Page<CollEntity> list(final String _startId, final int _count)
    {
        // Database operations
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final Page<CollEntity> _ret = this.collEntityRepository.list(_transaction, _startId, _count);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CollEntity get(final String _id)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final CollEntity _ret = this.collEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CollEntity create(final CollEntity _dto)
    {
        // Database operation
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final String _id = this.collEntityRepository.createNode(_transaction, _dto);
                final CollEntity _ret = this.collEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CollEntity update(final String _id, final CollEntity _dto)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.collEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CollEntity.class), _id);
                }
                this.collEntityRepository.updateNode(_transaction, _id, _dto);

                final CollEntity _ret = this.collEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    boolean delete(final String _id)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final boolean _ret = this.collEntityRepository.deleteNode(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }
}
