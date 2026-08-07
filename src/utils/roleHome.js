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
    default:
      return "/login";
  }
}
