var User = (function () {
    function User(_name) {
        this.name = _name;
    }
    User.prototype.sayHello = function () {
        return "Hello," + this.name + "!";
    };
    return User;
}());
var user = new User('John Reese');
user.sayHello();
