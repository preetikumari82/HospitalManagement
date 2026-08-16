export function roleHome(role) {
  switch (role) {
    case "ADMIN":
      return "/admin/doctors";
    case "DOCTOR":
      return "/doctor/patients";
    case "NURSE":
      return "/nurse/patients";
    case "MEDICAL":
      return "/medical/patients";
    case "RECEPTIONIST":
    case "PHARMACIST":
    case "LAB_TECH":
      return "/admin/patients";
    case "PATIENT":
      return "/patient/profile";
    default:
      return "/login";
  }
}
