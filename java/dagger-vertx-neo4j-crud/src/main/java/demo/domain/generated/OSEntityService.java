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
public class OSEntityService
{
    private static final Logger LOG = LoggerFactory.getLogger("svc.OSEntity");

    private final Driver driver;
    private final LinkRegistry linkRegistry;
    private final OSEntityRepository osEntityRepository;

    @Inject
    public OSEntityService(final Driver driver,
                           final LinkRegistry linkRegistry,
                           final OSEntityRepository osEntityRepository)
    {
        this.driver = driver;
        this.linkRegistry = linkRegistry;
        this.osEntityRepository = osEntityRepository;
    }

    public void init()
    {
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // ID is unique
                Neo4JHelper.createUniqueConstraint(_transaction, "MSEntity", "_id", LOG);

                // @Searchable @Required String name;
                Neo4JHelper.createIndex(_transaction, "MSEntity", "name", LOG);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    Page<OSEntity> list(final String _startId, final int _count)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final Page<OSEntity> _ret = this.osEntityRepository.list(_transaction, _startId, _count);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    OSEntity get(final String _id)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final OSEntity _ret = this.osEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    OSEntity create(final OSEntity _dto)
    {
        // Database operation
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final String _id = this.osEntityRepository.createNode(_transaction, _dto);
                final OSEntity _ret = this.osEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    OSEntity update(final String _id, final OSEntity _dto)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.osEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(OSEntity.class), _id);
                }
                this.osEntityRepository.updateNode(_transaction, _id, _dto);

                final OSEntity _ret = this.osEntityRepository.get(_transaction, _id);

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
                final boolean _ret = this.osEntityRepository.deleteNode(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }
}
