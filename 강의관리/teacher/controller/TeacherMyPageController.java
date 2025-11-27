package com.lms.urbangreen.urbangreenproject.teacher.controller;

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

@Controller
@RequestMapping("/teacher")
public class TeacherMyPageController {

    private final UserService userService;
    private final MypageAccountService mypageAccountService;

    public TeacherMyPageController(UserService userService, MypageAccountService mypageAccountService) {
        this.userService = userService;
        this.mypageAccountService = mypageAccountService;
    }

    @GetMapping({"", "/"})
    public String teacherMyPageRoot() {
        return "redirect:/teacher/teacherProfile";
    }

    // 공통: 세션에서 로그인 ID 조회 (MypageController와 동일)
    private String currentLoginId(HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj instanceof User u) return u.getId();
        Object loginId = session.getAttribute("loginId");
        return loginId instanceof String ? (String) loginId : null;
    }

    private String firstLetter(User u) {
        String s = (u.getNickname() != null && !u.getNickname().isBlank()) ? u.getNickname() : u.getName();
        return (s != null && !s.isBlank()) ? s.substring(0, 1).toUpperCase() : "T";
    }

    //내 프로필
    @GetMapping("/teacherProfile")
    public String profile(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("teacher", user);
        model.addAttribute("initial", firstLetter(user));   // ✅
        model.addAttribute("active", "teacherProfile");
        return "teacher/teacherProfile";
    }

    // GET: 내 정보수정 화면
    @GetMapping("/teacherEdit")
    public String edit(HttpSession session, Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("active", "teacherEdit"); // 사이드바 active
        return "teacher/teacherEdit"; // ↔ templates/mypage/edit.html
    }

    // POST: 프로필(이름/닉네임/이메일/생년월일/소개) 저장
    @PostMapping("/updateTeacherProfile")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String nickname,
                                @RequestParam String email,
                                @RequestParam(required = false) String birth, // "" 가능
                                @RequestParam(required = false) String intro,
                                HttpSession session,
                                Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        // 닉네임 중복(본인 제외) 체크
        if (userService.existsByNicknameExcludingUser(nickname, id)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "teacherEdit");
            model.addAttribute("error", "이미 사용 중인 닉네임입니다.");
            return "teacher/teacherEdit";
        }

        // birth 파싱 (빈값 → null)
        java.time.LocalDate birthDate = null;
        if (birth != null && !birth.isBlank()) {
            try {
                birthDate = java.time.LocalDate.parse(birth);
            } catch (Exception e) {
                model.addAttribute("user", userService.findById(id));
                model.addAttribute("active", "teacherEdit");
                model.addAttribute("error", "생년월일 형식이 올바르지 않습니다. (예: 2000-01-31)");
                return "teacher/teacherEdit";
            }
        }

        userService.updateProfile(id, name, nickname, email, birthDate, intro);
        return "redirect:/teacher/teacherEdit?success=1";
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
    @PostMapping("/teacherUpdatePassword")
    public String updatePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 Model model) {
        String id = currentLoginId(session);
        if (id == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "teacherEdit");
            model.addAttribute("pwError", "새 비밀번호와 확인이 일치하지 않습니다.");
            return "teacher/teacherEdit";
        }

        var result = userService.changePassword(id, currentPassword, newPassword);
        if (!result.success()) {
            model.addAttribute("user", userService.findById(id));
            model.addAttribute("active", "teacherEdit");
            model.addAttribute("pwError", result.message());
            return "teacher/teacherEdit";
        }

        return "redirect:/teacher/teacherEdit?pwChanged=1";
    }

    // 강사 댓글 보기
    @GetMapping("/teacherPosts")
    public String myCommentsPage(HttpSession session, Model model) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";   // 실제 로그인 URL 로 맞춰줘
        }

        model.addAttribute("active", "teacherPosts");  // 사이드바 active 처리
        return "teacher/teacherPosts";                  // templates/mypage/posts.html
    }
}
