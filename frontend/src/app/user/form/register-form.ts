import { FormControl } from "@angular/forms";

export interface RegisterForm {
    username: FormControl<String>;
    password: FormControl<String>;
    passwordRe: FormControl<String>;
}