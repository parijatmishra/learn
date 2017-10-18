package demo.domain.generated;

import lib.db.Page;
import lib.db.Reference;
import lib.exc.Api400InvalidReferenceException;
import lib.exc.Api404MissingResourceException;
import lib.exc.Api409ResourceInUseException;
import lib.neo4j.Neo4JHelper;
import lib.web.LinkRegistry;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Optional;

/**
 *
 */
public class MSOSEntityService
{
    private static final Logger LOG = LoggerFactory.getLogger("svc.MSOSEntity");

    private final Driver driver;
    private final LinkRegistry linkRegistry;
    private final MSOSEntityRepository msosEntityRepository;
    private final CoreEntityRepository coreEntityRepository;

    @Inject
    public MSOSEntityService(final Driver driver,
                             final LinkRegistry linkRegistry,
                             final MSOSEntityRepository msosEntityRepository,
                             final CoreEntityRepository coreEntityRepository)
    {
        this.driver = driver;
        this.linkRegistry = linkRegistry;
        this.msosEntityRepository = msosEntityRepository;
        this.coreEntityRepository = coreEntityRepository;
    }

    public void init()
    {
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // ID is unique
                Neo4JHelper.createUniqueConstraint(_transaction, "MSOSEntity", "_id", LOG);

                // @Searchable @Required String name;
                Neo4JHelper.createIndex(_transaction, "MSOSEntity", "name", LOG);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    Page<MSOSEntity> list(final String _startId, final int _count)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final Page<MSOSEntity> _ret = this.msosEntityRepository.list(_transaction, _startId, _count);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    MSOSEntity get(final String _id)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final MSOSEntity _ret = this.msosEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    MSOSEntity create(final MSOSEntity _dto)
    {
        // Database operation
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final String _id = this.msosEntityRepository.createNode(_transaction, _dto);
                final MSOSEntity _ret = this.msosEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    MSOSEntity update(final String _id, final MSOSEntity _dto)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.msosEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(MSOSEntity.class), _id);
                }
                this.msosEntityRepository.updateNode(_transaction, _id, _dto);

                final MSOSEntity _ret = this.msosEntityRepository.get(_transaction, _id);

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
                final boolean _ret = this.msosEntityRepository.deleteNode(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CoreEntity rel_coreEntity_get(String _id)
    {
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.msosEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(MSOSEntity.class), _id);
                }

                final CoreEntity _ret = this.msosEntityRepository.rel_coreEntity_get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    void rel_coreEntity_set(String _id, Reference<? extends CoreEntity> _reference)
    {
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.msosEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(MSOSEntity.class), _id);
                }

                rel_coreEntity_set(_transaction, _id, _reference);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    private void rel_coreEntity_set(final Transaction _transaction,
                                    final String _id,
                                    final Reference<? extends CoreEntity> _reference)
    {
        LOG.debug("rel_coreEntity_set: _id={} _target_id={}", _id, _reference.getId());

        // Target exists
        if (!this.coreEntityRepository.exists(_transaction, _reference.getId())) {
            throw new Api400InvalidReferenceException("coreEntity",
                                                      this.linkRegistry.getResourcePath(CoreEntity.class),
                                                      _reference.getId());
        }

        // Do we already have a rel?
        final Optional<String> coreEntity_id = this.msosEntityRepository.rel_coreEntity_getid(_transaction, _id);
        if (coreEntity_id.isPresent()) {
            LOG.debug("rel_coreEntity_set: _id={} connected to CoreEntity{_id={}}; cannot change ref",
                      _id,
                      coreEntity_id);
            throw new Api409ResourceInUseException(this.linkRegistry.getResourcePath(MSOSEntity.class), _id);
        }

        // Target connected to another rel? Delete the other rel
        final Optional<String> _other_id = this.coreEntityRepository.rel_msosEntity_getid(_transaction,
                                                                                          _reference.getId());
        if (_other_id.isPresent()) {
            this.msosEntityRepository.rel_coreEntity_delete(_transaction, _other_id.get());
            this.coreEntityRepository.rel_msosEntity_delete(_transaction, _reference.getId());
        }

        // Add new rel
        this.msosEntityRepository.rel_coreEntity_add(_transaction, _id, _reference);
        this.coreEntityRepository.rel_msosEntity_add(_transaction,
                                                     _reference.getId(),
                                                     new Reference<>(MSOSEntity.class, _id));
    }
}
