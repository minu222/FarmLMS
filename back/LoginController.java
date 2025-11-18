package com.lms.urbangreen.urbangreenproject.user.controller;

import com.lms.urbangreen.urbangreenproject.user.entity.User;
import com.lms.urbangreen.urbangreenproject.user.entity.UserType;
import com.lms.urbangreen.urbangreenproject.user.service.UserService;
import com.lms.urbangreen.urbangreenproject.user.web.form.ChangePwForm;
import com.lms.urbangreen.urbangreenproject.user.web.form.FindPwForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lms.urbangreen.urbangreenproject.user.web.form.FindIdForm;


@Controller
public class LoginController {

    private final UserService userService;
    private static final String PW_RESET_ID_SESSION_KEY = "PW_RESET_ID";

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // ==========================
    //   회원가입
    // ==========================
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "account/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(
            @ModelAttribute("user") User user,
            @RequestParam("passwordConfirm") String passwordConfirm,
            Model model,
            RedirectAttributes redirectAttributes   // ✅ 추가
    ) {
        boolean hasError = false;

        // 0. 기본값: 회원 타입(학생)
        if (user.getUserType() == null) {
            user.setUserType(UserType.student);
        }

        // 1. 아이디 검증: 공백 / 형식 / 중복
        if (user.getId() == null || user.getId().isBlank()) {
            model.addAttribute("idError", "아이디를 입력해 주세요.");
            hasError = true;
        } else if (!user.getId().matches("^[a-zA-Z0-9]{4,12}$")) {
            model.addAttribute("idError", "아이디는 영문과 숫자 4~12자로 입력해 주세요.");
            hasError = true;
        } else if (userService.existsById(user.getId())) {
            model.addAttribute("idError", "이미 존재하는 아이디입니다.");
            hasError = true;
        }

        // 2. 닉네임 검증: 공백 / 중복
        if (user.getNickname() == null || user.getNickname().isBlank()) {
            model.addAttribute("nicknameError", "닉네임을 입력해 주세요.");
            hasError = true;
        } else if (userService.existsByNickname(user.getNickname())) {
            model.addAttribute("nicknameError", "이미 존재하는 닉네임입니다.");
            hasError = true;
        }

        // 3. 비밀번호 검증: 공백 / 규칙 / 비밀번호 확인 일치 여부
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            model.addAttribute("passwordError", "비밀번호를 입력해 주세요.");
            hasError = true;
        } else if (!isValidPassword(user.getPassword())) {
            model.addAttribute("passwordError", "비밀번호는 영문과 숫자를 포함한 8자 이상이어야 합니다.");
            hasError = true;
        } else if (!user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            hasError = true;
        }

        // 4. 이름 필수
        if (user.getName() == null || user.getName().isBlank()) {
            model.addAttribute("nameError", "이름을 입력해 주세요.");
            hasError = true;
        }

        // 5. 이메일 필수 (간단히 공백만 체크)
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            model.addAttribute("emailError", "이메일을 입력해 주세요.");
            hasError = true;
        }

        // 6. 생년월일 필수 (DB에서 NOT NULL 이라서)
        if (user.getBirth() == null) {
            model.addAttribute("birthError", "생년월일을 입력해 주세요.");
            hasError = true;
        }

        // 하나라도 에러 있으면 다시 회원가입 페이지로
        if (hasError) {
            return "account/signup";
        }

        // 7. 회원 저장
        boolean success = userService.register(user);
        if (!success) {
            model.addAttribute("signupError", "회원가입 처리 중 오류가 발생했습니다. 다시 시도해 주세요.");
            return "account/signup";
        }

        // ✅ 회원가입 성공 메시지 (로그인 페이지에서 1회성으로 보여줌)
        redirectAttributes.addFlashAttribute(
                "signupSuccessMsg",
                "회원가입이 완료되었습니다. 로그인해 주세요."
        );


        // 8. 성공 시 로그인 페이지로 이동
        return "redirect:/login";
    }

    // 비밀번호 조건 검사 (영문 + 숫자, 8자 이상)
    private boolean isValidPassword(String password) {
        if (password == null) return false;
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    // 아이디 중복 체크 (AJAX)
    @GetMapping("/api/signup/check-id")
    @ResponseBody
    public boolean checkIdDuplicate(@RequestParam("id") String id) {
        // true = 사용 가능, false = 이미 사용 중
        return !userService.existsById(id);
    }

    // 닉네임 중복 체크 (AJAX)
    @GetMapping("/api/signup/check-nickname")
    @ResponseBody
    public boolean checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
        // true = 사용 가능, false = 이미 사용 중
        return !userService.existsByNickname(nickname);
    }


    // 로그인
    @GetMapping("/login")
    public String loginForm() {
        return "account/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, Model model, HttpSession session) {
        User found = userService.login(user.getId(), user.getPassword());
        if (found == null) {
            model.addAttribute("loginError", "아이디 또는 비밀번호가 틀립니다.");
            return "account/login";
        }

        // ✅ 로그인 성공 시 세션 저장
        session.setAttribute("loginUser", found);
        return "redirect:/";
    }


    //  로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제
        return "redirect:/main"; // 로그아웃 후 메인 페이지
    }

    // ==========================
//   아이디 찾기 (GET)
// ==========================
    @GetMapping("/findId")
    public String findIdForm(Model model) {
        model.addAttribute("findIdForm", new FindIdForm());
        return "account/findId";   // 템플릿: src/main/resources/templates/account/find-id.html
    }

    // ==========================
//   아이디 찾기 (POST)
// ==========================
    @PostMapping("/find-id")
    public String findIdSubmit(
            @ModelAttribute("findIdForm") FindIdForm form,
            Model model
    ) {
        // 1) 기본 검증 (이름/이메일 공백 체크)
        if (form.getName() == null || form.getName().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()) {

            model.addAttribute("errorMessage", "이름과 이메일을 모두 입력해 주세요.");
            return "account/findId";
        }

        // 2) 서비스 통해 조회
        User user = userService.findUserByNameAndEmail(form.getName(), form.getEmail());

        if (user == null) {
            // 일치하는 회원 없음
            model.addAttribute("errorMessage", "일치하는 회원 정보를 찾을 수 없습니다.");
        } else {
            // 아이디 찾음
            model.addAttribute("foundId", user.getId());
            // 가입일 컬럼이 DB에 없으니, 일단은 가입일은 표시하지 않거나
            // 필요하면 all_users에 created_at 컬럼 추가 후 여기서 같이 내려주면 됨.
            // model.addAttribute("joinedAt", "2025-01-01"); // 컬럼 생기면 이 부분 수정
        }

        // form 값은 @ModelAttribute로 이미 다시 바인딩된 상태라 그대로 유지됨
        return "account/findId";
    }

    // 비밀번호 찾기 (정보 입력 폼)
    @GetMapping("/findPassword")
    public String findPwForm(Model model) {
        model.addAttribute("findPwForm", new FindPwForm());
        return "account/findPw";   // templates/account/findPw.html
    }


    // 비밀번호 찾기 정보 확인 (POST)
    @PostMapping("/find-pw")
    public String findPwSubmit(
            @ModelAttribute("findPwForm") FindPwForm form,
            Model model,
            HttpSession session
    ) {
        // 간단 검증
        if (form.getId() == null || form.getId().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()) {
            model.addAttribute("errorMessage", "아이디와 이메일을 모두 입력해 주세요.");
            return "account/findPw";
        }

        User user = userService.findUserForPwReset(form.getId(), form.getEmail());
        if (user == null) {
            model.addAttribute("errorMessage", "일치하는 회원 정보를 찾을 수 없습니다.");
            return "account/findPw";
        }

        // 정보 일치 → 세션에 이 아이디 저장 후 비밀번호 변경 폼으로 이동
        session.setAttribute(PW_RESET_ID_SESSION_KEY, user.getId());
        return "redirect:/find-pw/change";
    }


    // 비밀번호 변경 폼 (비밀번호 찾기에서 넘어온 상태)
    @GetMapping("/find-pw/change")
    public String changePwFormFromFind(HttpSession session, Model model) {
        String targetId = (String) session.getAttribute(PW_RESET_ID_SESSION_KEY);
        if (targetId == null) {
            // 직접 URL 치고 들어온 경우 등
            return "redirect:/find-pw";
        }

        model.addAttribute("changePwForm", new ChangePwForm());
        return "account/changePw";   // 아래에서 만들 템플릿
    }


    // 비밀번호 변경 처리 (비밀번호 찾기 Flow)
    @PostMapping("/find-pw/change")
    public String changePwSubmitFromFind(
            @ModelAttribute("changePwForm") ChangePwForm form,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        String targetId = (String) session.getAttribute(PW_RESET_ID_SESSION_KEY);
        if (targetId == null) {
            return "redirect:/find-pw";
        }

        // 새 비밀번호 & 확인 일치 여부
        if (form.getNewPassword() == null || form.getConfirmPassword() == null
                || !form.getNewPassword().equals(form.getConfirmPassword())) {
            model.addAttribute("errorMessage", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
            return "account/changePw";
        }

        UserService.PwChangeResult result =
                userService.resetPasswordById(targetId, form.getNewPassword());

        if (!result.success()) {
            model.addAttribute("errorMessage", result.message());
            return "account/changePw";
        }

        // 성공 → 세션에서 제거 + 로그인 화면으로 이동하며 alert용 메시지 전달
        session.removeAttribute(PW_RESET_ID_SESSION_KEY);

        redirectAttributes.addFlashAttribute(
                "pwChangeSuccessMsg",
                "비밀번호가 변경되었습니다. 새 비밀번호로 로그인해 주세요."
        );
        return "redirect:/login";
    }
}

