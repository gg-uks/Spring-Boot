function login(){
  var formData= {
    username : document.getElementById("username").value,
    password : document.getElementById("password").value
  };
  fetch("/login",{
    method : "POST",
    headers: {
        "Content-Type":"application/json"
        },
    body:JSON.stringify(formData)
    })
  .then(response=>{
    if(!response.ok){
        throw new Error("Login failed");
        }
        //JWT 토큰을 받기
        var jwtToken = response.headers.get("Authorization") // public String HEADER_STRING="Authorization";  jwt 토큰의 헤더 스트링 키 값을 참조하여 가지고옴
        if(jwtToken && jwtToken.startsWith("Bearer ")){
           jwtToken = jwtToken.slice(7);// "Bearer " 문자열 7번(공백포함)짜리 다음부터 앞부분을 없애겠다 7번째까지
            localStorage.setItem("token",jwtToken);  // 로컬스토리지에 공백까지 합쳐진 7자리 문자열을 지운후 저장한 jwtToken으로  저장한다
            console.log("Received JWT token" + jwtToken); //?
            const authorities = jwtParsing(jwtToken)
            console.log("authorities" + authorities); //확인차 어떤데이터가 넘어오는지
            authorities.forEach(role=>{
                switch(role){
                    case "ROLE_USER" :
                        document.getElementById("userMenu").style.display = "block";
                        break;
                    case "ROLE_MANAGER" :
                        document.getElementById("managerMenu").style.display = "block";
                        break;
                    case "ROLE_ADMIN" :
                        document.getElementById("adminMenu").style.display = "block";
                        break;

                    default:
                        break;
                }

            });//forEach()
            //none login form | Display block
            document.getElementById("loginFom").style.display = "none";
            document.getElementById("greeting").style.display = "block";
            document.getElementById("usernameDisplay").innerText = formData.username;
        }else{
            console.log("invalid JWT token received");
        }
    })
  .catch(error=>{
  console.log("Login failed",error);
  });
}
function jwtParsing(token){ //[0].[1].[2]

    try{
    const tokenPayload = token.split(".")[1];
    const decodedPayload = atob(tokenPayload); //atob 로 디코드시킨다
    const payloadJSON = JSON.parse(decodedPayload);
    const authorities = payloadJSON.authorities; //JSON형식에서 authorities 키값을 통해 데이터를 가져온다
    return authorities; // [권한정보]
    }catch(error){
        console.log("error");
        return[];
    }
}
function logout(){
    localStorage.removeItem("token");
    location.href="/";


}