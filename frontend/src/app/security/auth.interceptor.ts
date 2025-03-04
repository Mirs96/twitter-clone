import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token =  localStorage.getItem('jwtToken');
  if (token) {
    req = req.clone({ // identical request by with token setted in the header
        setHeaders: {
          authorization: `Bearer ${token}`
        }
    });
  }
   
  return next(req); // to the next interceptor
};
