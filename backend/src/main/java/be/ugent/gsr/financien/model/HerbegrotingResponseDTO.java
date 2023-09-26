package be.ugent.gsr.financien.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Getter
@Setter
public class HerbegrotingResponseDTO extends AbstractAuditableDTO {

    private Integer id;

    @NotNull
    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "72.08")
    private BigDecimal hoeveelheid;

    @NotNull
    private boolean isForSubPosts;

    @NotNull
    private OffsetDateTime datum;

    @NotNull
    private Integer boekjaar;

    @NotNull
    private String fromPost;

    @NotNull
    private String toPost;

}
