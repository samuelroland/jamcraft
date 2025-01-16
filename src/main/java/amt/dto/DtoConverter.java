package amt.dto;

public interface DtoConverter<E, D> {

    // Converts a DTO to an entity
    E fromDTO(D dto);

    // Converts an entity to a DTO
    D toDTO(E entity);
}