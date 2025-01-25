package amt.dto;

/**
 * Interface for converting between Data Transfer Objects (DTOs) and entities.
 * Provides methods for bidirectional conversion to facilitate data mapping.
 *
 * @param <E> The type of the entity.
 * @param <D> The type of the DTO.
 * @author Yanis Ouadahi, Samuel Roland, Jarod Streckeisen, Timothée Van Hove
 */
public interface DtoConverter<E, D> {

    /**
     * Converts a DTO to its corresponding entity.
     *
     * @param dto The DTO to convert.
     * @return The entity created from the DTO.
     */
    E fromDTO(D dto);

    /**
     * Converts an entity to its corresponding DTO.
     *
     * @param entity The entity to convert.
     * @return The DTO created from the entity.
     */
    D toDTO(E entity);
}