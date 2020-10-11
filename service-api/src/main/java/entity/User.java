package entity;

import lombok.*;

/**
 * @author whc
 * @date 2020/10/11 20:47
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User {
    private String username;
    private String password;
    private Integer age;
}
