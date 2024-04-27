package com.tgtg.chat.socket.dto;

import com.tgtg.chat.anonymous.dto.AnonymousDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class GameRoleDTO {
    private String url;
    private Set<AnonymousDTO> roleList = new HashSet<>();
}
