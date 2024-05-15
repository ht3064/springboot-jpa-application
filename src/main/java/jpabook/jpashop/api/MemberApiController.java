package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();

        member.setName(request.getName());
        member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));
        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v3/members")
    public ResponseEntity<Long> saveMemberV3(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();

        member.setName(request.getName());
        member.setAddress(new Address(request.getCity(), request.getStreet(), request.getZipcode()));
        Long id = memberService.join(member);

        return ResponseEntity.ok(id);
    }

    @PatchMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @PatchMapping("/api/v3/members/{id}")
    public ResponseEntity<Long> updateMemberV3(@PathVariable("id") Long id,
                                               @RequestBody @Valid CreateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);

        return ResponseEntity.ok(findMember.getId());
    }

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {

        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {

        List<Member> findMembers = memberService.findMembers();
        //엔티티 -> DTO 변환
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .toList();

        return new Result(collect);
    }

    @AllArgsConstructor
    @Data
    static class CreateMemberResponse {

        private Long id;
    }

    @Data
    static class CreateMemberRequest {

        @NotEmpty
        private String name;

        private String city;
        private String street;
        private String zipcode;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {

        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest {

        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}
