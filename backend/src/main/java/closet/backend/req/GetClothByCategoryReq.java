package closet.backend.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Getter
@ToString
public class GetClothByCategoryReq {

	private String idToken;
	private String category;
}
