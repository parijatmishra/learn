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
public class MSEntityService
{
    private static final Logger LOG = LoggerFactory.getLogger("svc.MSEntity");

    private final Driver driver;
    private final LinkRegistry linkRegistry;
    private final MSEntityRepository msEntityRepository;

    @Inject
    public MSEntityService(final Driver driver,
                           final LinkRegistry linkRegistry,
                           final MSEntityRepository msEntityRepository)
    {
        this.driver = driver;
        this.linkRegistry = linkRegistry;
        this.msEntityRepository = msEntityRepository;
    }

    public void init()
    {
        try (Session _session = this.driver.session()) {
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

    Page<MSEntity> list(final String _startId, final int _count)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final Page<MSEntity> _ret = this.msEntityRepository.list(_transaction, _startId, _count);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    MSEntity get(final String _id)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final MSEntity _ret = this.msEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    MSEntity create(final MSEntity _dto)
    {
        // Database operation
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final String _id = this.msEntityRepository.createNode(_transaction, _dto);
                final MSEntity _ret = this.msEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    MSEntity update(final String _id, final MSEntity _dto)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.msEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(MSEntity.class), _id);
                }
                this.msEntityRepository.updateNode(_transaction, _id, _dto);

                final MSEntity _ret = this.msEntityRepository.get(_transaction, _id);

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
                final boolean _ret = this.msEntityRepository.deleteNode(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }
}
