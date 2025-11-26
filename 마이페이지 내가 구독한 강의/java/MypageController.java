package com.lms.urbangreen.urbangreenproject.mypage.controller;

import com.lms.urbangreen.urbangreenproject.lecture.entity.LectureListResponseDto;
import com.lms.urbangreen.urbangreenproject.lecture.service.LectureService;
import com.lms.urbangreen.urbangreenproject.mypage.service.MypageAccountService;
import com.lms.urbangreen.urbangreenproject.user.entity.User;
import com.lms.urbangreen.urbangreenproject.user.entity.UserType;
import com.lms.urbangreen.urbangreenproject.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage")
public class MypageController {

    private final UserService userService;
    private final MypageAccountService mypageAccountService;
    private final LectureService lectureService;

    public MypageController(UserService userService, MypageAccountService
                            mypageAccountService, LectureService lectureService) { this.userService = userService;
    this.mypageAccountService = mypageAccountService; this.lectureService = lectureService;}

    // 루트는 프로필로 리다이렉트
    @GetMapping({"", "/"})
    public String mypageRoot() {
        return "redirect:/mypage/profile";
    }

    // 공통: 세션 사용자 꺼내기 (loginUser 우선, 없으면 loginId 호환)
    private String currentLoginId(HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj instanceof User u) return u.getId();
        Object loginId = session.getAttribute("loginId");
        return loginId instanceof String ? (String) loginId : null;
    }

    private String firstLetter(User u){
        String s = (u.getNickname()!=null && !u.getNickname().isBlank()) ? u.getNickname() : u.getName();
        return (s!=null && !s.isBlank()) ? s.substring(0,1).toUpperCase() : "U";
    }


    //내 프로필
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model){
        String id = currentLoginId(session);
        if(id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("initial", firstLetter(user));   // ✅
        model.addAttribute("active", "profile");
        return "mypage/profile";
    }

    // GET: 내 정보수정 화면
    @GetMapping("/edit")
    public String edit(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("active", "edit"); // 사이드바 active
        return "mypage/edit"; // ↔ templates/mypage/edit.html
    }

    // POST: 프로필(이름/닉네임/이메일/생년월일/소개) 저장
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String nickname,
                                @RequestParam String email,
                                @RequestParam(required=false) String birth, // "" 가능
                                @RequestParam(required=false) String intro,
                                HttpSession session,
                                Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        // 닉네임 중복(본인 제외) 체크
        if (userService.existsByNicknameExcludingUser(nickname, id)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
            return "mypage/edit";
        }

        // birth 파싱 (빈값 → null)
        java.time.LocalDate birthDate = null;
        if (birth != null && !birth.isBlank()) {
            try { birthDate = java.time.LocalDate.parse(birth); }
            catch (Exception e) {
                model.addAttribute("user", userService.findById(id));
                model.addAttribute("active", "edit");
                model.addAttribute("error", "생년월일 형식이 올바르지 않습니다. (예: 2000-01-31)");
                return "mypage/edit";
            }
        }

        userService.updateProfile(id, name, nickname, email, birthDate, intro);
        return "redirect:/mypage/edit?success=1";
    }


    /**
     * 회원 탈퇴
     * POST /mypage/delete
     */
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteAccount(HttpSession session) {

        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            // 로그인 안 되어 있음
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User loginUser = (User) loginUserObj;

        // 🔥 관리자 방어 로직 (UserType enum 기준)
        UserType userType = loginUser.getUserType();
        if (userType == UserType.admin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        int userId = loginUser.getUserId();  // 필드명/메서드명은 프로젝트에 맞게 조정

        mypageAccountService.deleteAccount(userId);

        // 세션 로그아웃
        session.invalidate();

        return ResponseEntity.ok().build();
    }

    // POST: 비밀번호 변경
    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("pwError", "새 비밀번호와 확인이 일치하지 않습니다.");
            return "mypage/edit";
        }

        var result = userService.changePassword(id, currentPassword, newPassword);
        if (!result.success()) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "edit");
            model.addAttribute("pwError", result.message());
            return "mypage/edit";
        }

        return "redirect:/mypage/edit?pwChanged=1";
    }

    //내가 구독한 강의
    @GetMapping("/subscriptions")
    public String subscriptions(HttpSession session, Model model) {
        User loginUser = getCurrentLoginUser(session);
        if (loginUser == null) return "redirect:/login";

        // 1. 사용자 ID로 구독 강의 목록 DTO 조회 (이전 답변에서 LectureService에 추가한 메서드 사용)
        List<LectureListResponseDto> subscribedLectureDtos =
                lectureService.getSubscribedLectureDtos(loginUser.getUserId());

        // 2. 뷰에 데이터 전달
        model.addAttribute("lectures", subscribedLectureDtos);
        model.addAttribute("user", loginUser); // 템플릿의 사이드바/헤더용 사용자 정보
        model.addAttribute("active", "subscriptions"); // 사이드바 활성화

        // 3. JS 구독 상태 확인을 위해 구독된 강의 ID 목록을 Set으로 전달 (필수는 아니지만 일관성을 위해 추가)
        List<Integer> subscribedLectureIds = subscribedLectureDtos.stream()
                .map(LectureListResponseDto::getLecture_id)
                .collect(Collectors.toList());

        model.addAttribute("subscribedLectureIds", subscribedLectureIds);

        return "mypage/subscriptions"; // templates/mypage/subscriptions.html
    }


    // 내 댓글 보기
    @GetMapping("/posts")
    public String myCommentsPage(HttpSession session, Model model) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";   // 실제 로그인 URL 로 맞춰줘
        }

        model.addAttribute("active", "posts");  // 사이드바 active 처리
        return "mypage/posts";                  // templates/mypage/posts.html
    }

    @GetMapping("/check-nickname")
    @ResponseBody
    public Map<String, Boolean> checkNickname(@RequestParam String nickname) {
        boolean available = !userService.existsByNickname(nickname);
        return Map.of("available", available);
    }

    private User getCurrentLoginUser(HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        return loginUserObj instanceof User ? (User) loginUserObj : null;
    }

}
