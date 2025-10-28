package ng.darum.commons.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DefaultService - A reusable, generic service layer
 * -----------------------------------------------------
 * This class provides basic CRUD, pagination, and property-copying logic
 * that can be extended by any service in your microservices ecosystem.
 *
 *  Key Features:
 *  - Simplifies repetitive CRUD logic.
 *  - Optionally supports DTO-to-Entity and Entity-to-Response conversion.
 *  - Includes safe property copying to prevent overwriting nulls.
 *  - Easily extendable for domain-specific overrides.
 *
 *  Example:
 *  public class EmployeeService extends DefaultService<Employee, Long, EmployeeRequest, EmployeeResponse> {
 *      public EmployeeService(EmployeeRepository repository) { super(repository); }
 *  }
 */

@Slf4j
public abstract class DefaultService<
        E, ID, Req, Res> {

    protected final JpaRepository<E, ID> repository;

    public DefaultService(JpaRepository<E, ID> repository) {
        this.repository = repository;
    }

    /* =====================================================
       CREATE METHODS
       ===================================================== */

    /**
     * Create an entity directly (without DTO).
     */
    public E create(E entity) {
        log.info("Creating entity: {}", entity);
        return repository.save(entity);
    }

    /**
     * Create from DTO and return response DTO (optional).
     * Override toEntity() and toResponse() in subclass if using DTOs.
     */
    public Res createFromDto(Req dto) {
        log.info("Creating entity from DTO: {}", dto);
        E entity = toEntity(dto);
        E saved = repository.save(entity);
        return toResponse(saved);
    }

    /* =====================================================
       UPDATE METHODS
       ===================================================== */

    /**
     * Update an entity partially — copies only non-null fields.
     */
    public E update(ID id, E updateData) {
        log.info("Updating entity with ID: {}", id);
        E existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with ID: " + id));

        copyNonNullProperties(updateData, existing);
        return repository.save(existing);
    }

    /**
     * Update from DTO — if subclass implements conversion.
     */
    public Res updateFromDto(ID id, Req dto) {
        log.info("Updating entity with DTO for ID: {}", id);
        E entity = toEntity(dto);
        return toResponse(update(id, entity));
    }

    /* =====================================================
        DELETE METHODS
       ===================================================== */

    /**
     * Delete an entity by ID.
     */
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Entity not found with ID: " + id);
        }
        repository.deleteById(id);
        log.info("Deleted entity with ID: {}", id);
    }

    /* =====================================================
        FETCH METHODS
       ===================================================== */

    /**
     * Get entity by ID.
     */
    public E getById(ID id) {
        Optional<E> entity = repository.findById(id);
        return entity.orElseThrow(() -> new RuntimeException("Entity not found with ID: " + id));
    }

    /**
     * Get all entities (non-paginated).
     */
    public List<E> getAll() {
        return repository.findAll();
    }

    /**
     * Get all entities with pagination.
     */
    public List<E> getAllPaginated(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    /* =====================================================
        OPTIONAL DTO CONVERSIONS
       ===================================================== */

    /**
     * Convert DTO → Entity. Override this in subclass if using DTOs.
     */
    protected E toEntity(Req dto) {
        throw new UnsupportedOperationException("DTO to Entity conversion not implemented.");
    }

    /**
     * Convert Entity → Response DTO. Override this in subclass if using DTOs.
     */
    public Res toResponse(E entity) {
        throw new UnsupportedOperationException("Entity to DTO conversion not implemented.");
    }

    /**
     * Get all as DTOs (if toResponse() is implemented).
     */
    public List<Res> getAllAsDto() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* =====================================================
        UTILITY: SAFE PROPERTY COPYING
       ===================================================== */

    /**
     * Copies all non-null fields from src → target.
     */
    protected void copyNonNullProperties(Object src, Object target) {
        try {
            final BeanWrapperImpl srcWrap = new BeanWrapperImpl(src);
            final BeanWrapperImpl trgWrap = new BeanWrapperImpl(target);

            Arrays.stream(srcWrap.getPropertyDescriptors()).forEach(pd -> {
                String name = pd.getName();
                if ("class".equals(name)) return;
                Object value = srcWrap.getPropertyValue(name);
                if (value != null && trgWrap.isWritableProperty(name)) {
                    trgWrap.setPropertyValue(name, value);
                }
            });

        } catch (Exception e) {
            log.error("Error copying properties", e);
            throw new RuntimeException("Failed to copy properties: " + e.getMessage(), e);
        }
    }
}

